package com.allawixron.journeyguide.journey;

import com.allawixron.journeyguide.Config;
import com.allawixron.journeyguide.build.Blueprint;
import com.allawixron.journeyguide.build.BlueprintPlacer;
import com.allawixron.journeyguide.build.Blueprints;
import com.allawixron.journeyguide.build.BuildCheck;
import com.allawixron.journeyguide.network.ActionPacket;
import com.allawixron.journeyguide.network.Net;
import com.allawixron.journeyguide.network.SyncPacket;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Server side of the journey: tracks every player's progress, checks builds and syncs to the client. */
public final class JourneyServer {
    private static final String DATA_KEY = "journeyguide";
    private static final int CHECK_INTERVAL = 20;
    private static final int BUILD_CHECK_INTERVAL = 40;
    private static final int RESYNC_TICKS = 100;

    private static final Map<UUID, PlayerProgress> CACHE = new HashMap<>();

    private JourneyServer() {}

    public static void init() {
        TickEvent.PlayerTickEvent.Post.BUS.addListener(JourneyServer::onPlayerTick);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(e -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                PlayerProgress p = get(player);
                p.lastSyncHash = 0;
                update(player, p);
            }
        });
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(e -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                save(player, get(player));
                CACHE.remove(player.getUUID());
            }
        });
        PlayerEvent.PlayerChangedDimensionEvent.BUS.addListener(e -> {
            if (e.getEntity() instanceof ServerPlayer player) {
                PlayerProgress p = get(player);
                p.pointerSearched = false;
                p.lastSyncHash = 0;
            }
        });
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(e -> {
            if (e.getEntity() instanceof ServerPlayer player)
                get(player).lastSyncHash = 0;
        });
        RegisterCommandsEvent.BUS.addListener(JourneyServer::registerCommands);
    }

    // ------------------------------------------------------------------ storage

    public static PlayerProgress get(ServerPlayer player) {
        return CACHE.computeIfAbsent(player.getUUID(), uuid -> {
            CompoundTag persisted = player.getPersistentData().getCompoundOrEmpty(ServerPlayer.PERSISTED_NBT_TAG);
            return PlayerProgress.load(persisted.getCompoundOrEmpty(DATA_KEY));
        });
    }

    private static void save(ServerPlayer player, PlayerProgress p) {
        CompoundTag root = player.getPersistentData();
        CompoundTag persisted = root.getCompoundOrEmpty(ServerPlayer.PERSISTED_NBT_TAG);
        persisted.put(DATA_KEY, p.save());
        root.put(ServerPlayer.PERSISTED_NBT_TAG, persisted);
    }

    // ------------------------------------------------------------------ ticking

    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (!(event.player() instanceof ServerPlayer player))
            return;
        if (player.tickCount % CHECK_INTERVAL != 0)
            return;

        PlayerProgress p = get(player);
        p.ticksOnStep += CHECK_INTERVAL;
        p.ticksSinceSync += CHECK_INTERVAL;

        if (p.activeBuild != null && player.tickCount % BUILD_CHECK_INTERVAL == 0)
            checkBuild(player, p);

        update(player, p);
    }

    /** Re-checks all goals, unlocks hints and syncs to the client if anything changed. */
    public static void update(ServerPlayer player, PlayerProgress p) {
        Map<String, Integer> progress = new HashMap<>();
        boolean changed = false;
        for (Step step : Journey.steps()) {
            if (p.completed.contains(step.id()))
                continue;
            int target = step.goal().target();
            int value = Math.min(step.goal().progress(player, p), target);
            if (value >= target) {
                p.completed.add(step.id());
                changed = true;
            } else {
                progress.put(step.id(), value);
            }
        }

        Step current = currentStep(p);
        String currentId = current == null ? "" : current.id();
        if (!currentId.equals(p.currentStep)) {
            p.resetStepTimer(currentId);
            changed = true;
        }

        if (current != null)
            maybeFindPointer(player, p, current);

        if (!p.welcomed) {
            p.welcomed = true;
            changed = true;
            player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Your guide to beating Minecraft is on the top left. Press ")
                            .withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("J").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                    .append(Component.literal(" to open your journal and see every step.").withStyle(ChatFormatting.YELLOW)));
        }

        if (changed)
            save(player, p);

        SyncPacket packet = toPacket(p, progress);
        int hash = packet.contentHash();
        if (hash != p.lastSyncHash || p.ticksSinceSync >= RESYNC_TICKS) {
            p.lastSyncHash = hash;
            p.ticksSinceSync = 0;
            Net.sendTo(player, packet);
        }
    }

    public static @Nullable Step currentStep(PlayerProgress p) {
        for (Step step : Journey.steps())
            if (!p.completed.contains(step.id()))
                return step;
        return null;
    }

    private static void maybeFindPointer(ServerPlayer player, PlayerProgress p, Step step) {
        Step.Locate locate = step.locate();
        if (locate == null || p.pointerSearched)
            return;
        if (p.ticksOnStep < Config.POINTER_MINUTES.get() * 60 * 20)
            return;
        ServerLevel level = player.level();
        if (level.dimension() != locate.dimension())
            return;

        p.pointerSearched = true;
        BlockPos found = level.findNearestMapStructure(locate.structures(), player.blockPosition(), Config.POINTER_SEARCH_CHUNKS.get(), false);
        if (found != null) {
            p.pointer = new PlayerProgress.Pointer(found, level.dimension().identifier(), locate.name());
            player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Stuck? Follow the arrow on your tracker to the nearest " + locate.name() + ".")
                            .withStyle(ChatFormatting.YELLOW)));
        }
    }

    private static void checkBuild(ServerPlayer player, PlayerProgress p) {
        PlayerProgress.ActiveBuild build = p.activeBuild;
        Blueprint bp = build == null ? null : Blueprints.get(build.blueprintId());
        if (bp == null) {
            p.activeBuild = null;
            return;
        }
        ServerLevel level = player.level();
        if (!level.dimension().identifier().equals(build.dimension()))
            return;

        List<Blueprint.Cell> cells = bp.cells(build.origin(), build.rotation());
        for (Blueprint.Cell cell : cells)
            if (!level.isLoaded(cell.pos()))
                return;

        if (BuildCheck.isComplete(level, cells)) {
            p.completedBuilds.add(bp.id);
            p.activeBuild = null;
            save(player, p);
            player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Build complete: " + bp.name + "! Great work.").withStyle(ChatFormatting.GREEN)));
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }

    private static SyncPacket toPacket(PlayerProgress p, Map<String, Integer> progress) {
        SyncPacket.Pointer pointer = p.pointer == null ? null
                : new SyncPacket.Pointer(p.pointer.pos(), p.pointer.dimension(), p.pointer.name());
        SyncPacket.Build build = p.activeBuild == null ? null
                : new SyncPacket.Build(p.activeBuild.blueprintId(), p.activeBuild.origin(), p.activeBuild.rotation(), p.activeBuild.dimension());
        return new SyncPacket(
                new ArrayList<>(p.completed), new ArrayList<>(p.skipped), new ArrayList<>(p.completedBuilds),
                progress, p.currentStep, p.ticksOnStep, p.extraTips,
                Config.EXTRA_TIP_MINUTES.get() * 60 * 20, Config.POINTER_MINUTES.get() * 60 * 20,
                pointer, build);
    }

    // ------------------------------------------------------------------ actions from the client

    public static void handleAction(ServerPlayer player, ActionPacket msg) {
        PlayerProgress p = get(player);
        switch (msg.action()) {
            case SKIP_STEP -> {
                if (Journey.get(msg.text()) != null && p.completed.add(msg.text()))
                    p.skipped.add(msg.text());
            }
            case UNSKIP_STEP -> {
                if (p.skipped.remove(msg.text()))
                    p.completed.remove(msg.text());
            }
            case MORE_TIPS -> p.extraTips++;
            case PLACE_BUILD -> {
                Blueprint bp = Blueprints.get(msg.text());
                if (bp == null || msg.pos().distSqr(player.blockPosition()) > 96 * 96)
                    return;
                p.activeBuild = new PlayerProgress.ActiveBuild(bp.id, msg.pos(), msg.rotation(), player.level().dimension().identifier());
            }
            case CANCEL_BUILD -> p.activeBuild = null;
            case PLACE_BLOCK -> {
                PlayerProgress.ActiveBuild build = p.activeBuild;
                Blueprint bp = build == null ? null : Blueprints.get(build.blueprintId());
                if (bp == null || !player.level().dimension().identifier().equals(build.dimension()))
                    return;
                InteractionHand hand = msg.text().equals("off") ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                if (BlueprintPlacer.place(player, bp, build.origin(), build.rotation(), msg.pos(), hand))
                    checkBuild(player, p);
                return;
            }
        }
        save(player, p);
        update(player, p);
    }

    // ------------------------------------------------------------------ commands

    private static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("journey")
                .executes(JourneyServer::cmdStatus)
                .then(Commands.literal("skip").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    Step step = currentStep(get(player));
                    if (step != null)
                        handleAction(player, ActionPacket.skip(step.id()));
                    return cmdStatus(ctx);
                }))
                .then(Commands.literal("complete")
                        .then(Commands.argument("step", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        Journey.steps().stream().map(Step::id), builder))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String id = StringArgumentType.getString(ctx, "step");
                                    if (Journey.get(id) == null) {
                                        ctx.getSource().sendFailure(Component.literal("Unknown step: " + id));
                                        return 0;
                                    }
                                    handleAction(player, ActionPacket.skip(id));
                                    return cmdStatus(ctx);
                                })))
                .then(Commands.literal("buildnow")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(JourneyServer::cmdBuildNow))
                .then(Commands.literal("reset").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PlayerProgress fresh = new PlayerProgress();
                    fresh.welcomed = true;
                    CACHE.put(player.getUUID(), fresh);
                    save(player, fresh);
                    update(player, fresh);
                    ctx.getSource().sendSuccess(() -> Component.literal("Journey reset. Steps you already did will tick off again by themselves."), false);
                    return 1;
                })));
    }

    private static int cmdStatus(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        PlayerProgress p = get(player);
        Step step = currentStep(p);
        int done = p.completed.size();
        int total = Journey.steps().size();
        if (step == null) {
            ctx.getSource().sendSuccess(() -> Component.literal("You finished the whole journey! (" + done + "/" + total + ")")
                    .withStyle(ChatFormatting.GOLD), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Step " + (Journey.indexOf(step) + 1) + "/" + total + ": ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(step.title() + " - " + step.summary()).withStyle(ChatFormatting.WHITE)), false);
        }
        return 1;
    }

    /** Creative helper: finishes the current build instantly (needs cheats). */
    private static int cmdBuildNow(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        PlayerProgress p = get(player);
        Blueprint bp = p.activeBuild == null ? null : Blueprints.get(p.activeBuild.blueprintId());
        if (bp == null) {
            ctx.getSource().sendFailure(Component.literal("No build in progress. Place one from the journal (J > Builds) first."));
            return 0;
        }
        ServerLevel level = player.level();
        List<Blueprint.Cell> cells = bp.cells(p.activeBuild.origin(), p.activeBuild.rotation());
        for (Blueprint.Cell cell : cells)
            level.setBlock(cell.pos(), cell.state(), Block.UPDATE_CLIENTS);
        // second pass so fences, panes and stairs connect to their neighbours
        for (Blueprint.Cell cell : cells) {
            if (cell.mustBeEmpty())
                continue;
            // keep blocks that would pop off (like a torch over a dip in the ground) instead of losing them
            BlockState connected = Block.updateFromNeighbourShapes(cell.state(), level, cell.pos());
            if (!connected.isAir())
                level.setBlock(cell.pos(), connected, Block.UPDATE_CLIENTS);
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Built " + bp.name + " instantly."), false);
        checkBuild(player, p);
        update(player, p);
        return 1;
    }
}
