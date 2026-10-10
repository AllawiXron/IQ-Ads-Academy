package xallawix.mod.journeyguide.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.build.Blueprints;
import xallawix.mod.journeyguide.build.BuildCheck;
import xallawix.mod.journeyguide.journey.PlayerProgress;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;

import java.util.List;

public class CheckBuildProcedure {
	public static void execute(Entity entity) {
		if (!(entity instanceof ServerPlayer player))
			return;
		PlayerProgress progress = JourneyGuideModVariables.get(player);
		PlayerProgress.ActiveBuild build = progress.activeBuild;
		Blueprint blueprint = build == null ? null : Blueprints.get(build.blueprintId());
		if (blueprint == null) {
			progress.activeBuild = null;
			return;
		}
		ServerLevel level = player.level();
		if (!level.dimension().identifier().equals(build.dimension()))
			return;

		List<Blueprint.Cell> cells = blueprint.cells(build.origin(), build.rotation());
		for (Blueprint.Cell cell : cells) {
			if (!level.isLoaded(cell.pos()))
				return;
		}
		if (BuildCheck.isComplete(level, cells)) {
			progress.completedBuilds.add(blueprint.id);
			progress.activeBuild = null;
			JourneyGuideModVariables.save(player, progress);
			player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
					.append(Component.literal("Build complete: " + blueprint.name + "! Great work.").withStyle(ChatFormatting.GREEN)));
			level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.0F);
		}
	}
}
