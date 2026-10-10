package xallawix.mod.journeyguide.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import xallawix.mod.journeyguide.configuration.JourneyGuideConfiguration;
import xallawix.mod.journeyguide.journey.PlayerProgress;
import xallawix.mod.journeyguide.journey.Step;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;

public class FindStructurePointerProcedure {
	public static void execute(Entity entity, Step step) {
		if (!(entity instanceof ServerPlayer player))
			return;
		PlayerProgress progress = JourneyGuideModVariables.get(player);
		Step.Locate locate = step.locate();
		if (locate == null || progress.pointerSearched)
			return;
		if (progress.ticksOnStep < JourneyGuideConfiguration.POINTER_MINUTES.get() * 60 * 20)
			return;
		ServerLevel level = player.level();
		if (level.dimension() != locate.dimension())
			return;

		progress.pointerSearched = true;
		BlockPos found = level.findNearestMapStructure(locate.structures(), player.blockPosition(), JourneyGuideConfiguration.POINTER_SEARCH_CHUNKS.get(), false);
		if (found != null) {
			progress.pointer = new PlayerProgress.Pointer(found, level.dimension().identifier(), locate.name());
			player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
					.append(Component.literal("Stuck? Follow the arrow on your tracker to the nearest " + locate.name() + ".").withStyle(ChatFormatting.YELLOW)));
		}
	}
}
