package xallawix.mod.journeyguide.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** The player's whole journey state, sent from the server whenever something changes. */
public record SyncPacket(
        List<String> completed,
        List<String> skipped,
        List<String> completedBuilds,
        Map<String, Integer> progress,
        String currentStep,
        int ticksOnStep,
        int extraTips,
        int extraTipTicks,
        int pointerTicks,
        @Nullable Pointer pointer,
        @Nullable Build build
) {
    public record Pointer(BlockPos pos, Identifier dimension, String name) {}

    public record Build(String blueprintId, BlockPos origin, Rotation rotation, Identifier dimension) {}

    /** Hash of everything except the ticking timer, to know when a new sync is worth sending. */
    public int contentHash() {
        return Objects.hash(completed, skipped, completedBuilds, progress, currentStep, extraTips, pointer, build);
    }

    public static void encode(SyncPacket msg, RegistryFriendlyByteBuf buf) {
        writeList(buf, msg.completed);
        writeList(buf, msg.skipped);
        writeList(buf, msg.completedBuilds);
        buf.writeVarInt(msg.progress.size());
        msg.progress.forEach((k, v) -> {
            buf.writeUtf(k);
            buf.writeVarInt(v);
        });
        buf.writeUtf(msg.currentStep);
        buf.writeVarInt(msg.ticksOnStep);
        buf.writeVarInt(msg.extraTips);
        buf.writeVarInt(msg.extraTipTicks);
        buf.writeVarInt(msg.pointerTicks);

        buf.writeBoolean(msg.pointer != null);
        if (msg.pointer != null) {
            buf.writeBlockPos(msg.pointer.pos());
            buf.writeUtf(msg.pointer.dimension().toString());
            buf.writeUtf(msg.pointer.name());
        }

        buf.writeBoolean(msg.build != null);
        if (msg.build != null) {
            buf.writeUtf(msg.build.blueprintId());
            buf.writeBlockPos(msg.build.origin());
            buf.writeVarInt(msg.build.rotation().ordinal());
            buf.writeUtf(msg.build.dimension().toString());
        }
    }

    public static SyncPacket decode(RegistryFriendlyByteBuf buf) {
        List<String> completed = readList(buf);
        List<String> skipped = readList(buf);
        List<String> builds = readList(buf);
        int n = buf.readVarInt();
        Map<String, Integer> progress = new HashMap<>();
        for (int i = 0; i < n; i++)
            progress.put(buf.readUtf(), buf.readVarInt());
        String current = buf.readUtf();
        int ticks = buf.readVarInt();
        int extraTips = buf.readVarInt();
        int extraTipTicks = buf.readVarInt();
        int pointerTicks = buf.readVarInt();

        Pointer pointer = null;
        if (buf.readBoolean())
            pointer = new Pointer(buf.readBlockPos(), parse(buf.readUtf()), buf.readUtf());

        Build build = null;
        if (buf.readBoolean()) {
            String id = buf.readUtf();
            BlockPos origin = buf.readBlockPos();
            Rotation[] rotations = Rotation.values();
            Rotation rotation = rotations[Math.floorMod(buf.readVarInt(), rotations.length)];
            build = new Build(id, origin, rotation, parse(buf.readUtf()));
        }
        return new SyncPacket(completed, skipped, builds, progress, current, ticks, extraTips, extraTipTicks, pointerTicks, pointer, build);
    }

    private static Identifier parse(String s) {
        Identifier id = Identifier.tryParse(s);
        return id == null ? Identifier.withDefaultNamespace("overworld") : id;
    }

    private static void writeList(RegistryFriendlyByteBuf buf, List<String> list) {
        buf.writeVarInt(list.size());
        for (String s : list)
            buf.writeUtf(s);
    }

    private static List<String> readList(RegistryFriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            list.add(buf.readUtf());
        return list;
    }
}
