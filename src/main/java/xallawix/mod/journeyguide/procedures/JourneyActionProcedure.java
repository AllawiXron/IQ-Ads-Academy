package xallawix.mod.journeyguide.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.build.BlueprintPlacer;
import xallawix.mod.journeyguide.build.Blueprints;
import xallawix.mod.journeyguide.journey.Journey;
import xallawix.mod.journeyguide.journey.PlayerProgress;
import xallawix.mod.journeyguide.network.JourneyActionMessage;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;

public class JourneyActionProcedure {
	public static void execute(Entity entity, JourneyActionMessage message) {
		if (!(entity instanceof ServerPlayer player))
			return;
		PlayerProgress progress = JourneyGuideModVariables.get(player);
		switch (message.action()) {
			case SKIP_STEP -> {
				if (Journey.get(message.text()) != null && progress.completed.add(message.text()))
					progress.skipped.add(message.text());
			}
			case UNSKIP_STEP -> {
				if (progress.skipped.remove(message.text()))
					progress.completed.remove(message.text());
			}
			case MORE_TIPS -> progress.extraTips++;
			case PLACE_BUILD -> {
				Blueprint blueprint = Blueprints.get(message.text());
				if (blueprint == null || message.pos().distSqr(player.blockPosition()) > 96 * 96)
					return;
				progress.activeBuild = new PlayerProgress.ActiveBuild(blueprint.id, message.pos(), message.rotation(), player.level().dimension().identifier());
			}
			case CANCEL_BUILD -> progress.activeBuild = null;
			case PLACE_BLOCK -> {
				PlayerProgress.ActiveBuild build = progress.activeBuild;
				Blueprint blueprint = build == null ? null : Blueprints.get(build.blueprintId());
				if (blueprint == null || !player.level().dimension().identifier().equals(build.dimension()))
					return;
				InteractionHand hand = message.text().equals("off") ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
				if (BlueprintPlacer.place(player, blueprint, build.origin(), build.rotation(), message.pos(), hand))
					CheckBuildProcedure.execute(player);
				return;
			}
		}
		JourneyGuideModVariables.save(player, progress);
		UpdateJourneyProcedure.execute(player);
	}
}
