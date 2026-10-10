package xallawix.mod.journeyguide.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.build.Blueprints;
import xallawix.mod.journeyguide.journey.Journey;
import xallawix.mod.journeyguide.journey.PlayerProgress;
import xallawix.mod.journeyguide.journey.Step;
import xallawix.mod.journeyguide.network.JourneyActionMessage;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;
import xallawix.mod.journeyguide.procedures.CheckBuildProcedure;
import xallawix.mod.journeyguide.procedures.JourneyActionProcedure;
import xallawix.mod.journeyguide.procedures.UpdateJourneyProcedure;

import java.util.List;

@Mod.EventBusSubscriber
public class JourneyCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("journey")
				.executes(JourneyCommand::status)
				.then(Commands.literal("skip").executes(ctx -> {
					ServerPlayer player = ctx.getSource().getPlayerOrException();
					Step step = UpdateJourneyProcedure.currentStep(JourneyGuideModVariables.get(player));
					if (step != null)
						JourneyActionProcedure.execute(player, JourneyActionMessage.skip(step.id()));
					return status(ctx);
				}))
				.then(Commands.literal("complete").then(Commands.argument("step", StringArgumentType.word())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(Journey.steps().stream().map(Step::id), builder))
						.executes(ctx -> {
							ServerPlayer player = ctx.getSource().getPlayerOrException();
							String id = StringArgumentType.getString(ctx, "step");
							if (Journey.get(id) == null) {
								ctx.getSource().sendFailure(Component.literal("Unknown step: " + id));
								return 0;
							}
							JourneyActionProcedure.execute(player, JourneyActionMessage.skip(id));
							return status(ctx);
						})))
				.then(Commands.literal("buildnow").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(JourneyCommand::buildNow))
				.then(Commands.literal("reset").executes(ctx -> {
					ServerPlayer player = ctx.getSource().getPlayerOrException();
					PlayerProgress fresh = new PlayerProgress();
					fresh.welcomed = true;
					JourneyGuideModVariables.set(player, fresh);
					UpdateJourneyProcedure.execute(player);
					ctx.getSource().sendSuccess(() -> Component.literal("Journey reset. Steps you already did will tick off again by themselves."), false);
					return 1;
				})));
	}

	private static int status(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		PlayerProgress progress = JourneyGuideModVariables.get(player);
		Step step = UpdateJourneyProcedure.currentStep(progress);
		int done = progress.completed.size();
		int total = Journey.steps().size();
		if (step == null) {
			ctx.getSource().sendSuccess(() -> Component.literal("You finished the whole journey! (" + done + "/" + total + ")").withStyle(ChatFormatting.GOLD), false);
		} else {
			ctx.getSource().sendSuccess(() -> Component.literal("Step " + (Journey.indexOf(step) + 1) + "/" + total + ": ").withStyle(ChatFormatting.GOLD)
					.append(Component.literal(step.title() + " - " + step.summary()).withStyle(ChatFormatting.WHITE)), false);
		}
		return 1;
	}

	private static int buildNow(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		PlayerProgress progress = JourneyGuideModVariables.get(player);
		Blueprint blueprint = progress.activeBuild == null ? null : Blueprints.get(progress.activeBuild.blueprintId());
		if (blueprint == null) {
			ctx.getSource().sendFailure(Component.literal("No build in progress. Place one from the journal (J > Builds) first."));
			return 0;
		}
		ServerLevel level = player.level();
		List<Blueprint.Cell> cells = blueprint.cells(progress.activeBuild.origin(), progress.activeBuild.rotation());
		for (Blueprint.Cell cell : cells)
			level.setBlock(cell.pos(), cell.state(), Block.UPDATE_CLIENTS);
		for (Blueprint.Cell cell : cells) {
			if (cell.mustBeEmpty())
				continue;
			BlockState connected = Block.updateFromNeighbourShapes(cell.state(), level, cell.pos());
			if (!connected.isAir())
				level.setBlock(cell.pos(), connected, Block.UPDATE_CLIENTS);
		}
		ctx.getSource().sendSuccess(() -> Component.literal("Built " + blueprint.name + " instantly."), false);
		CheckBuildProcedure.execute(player);
		UpdateJourneyProcedure.execute(player);
		return 1;
	}
}
