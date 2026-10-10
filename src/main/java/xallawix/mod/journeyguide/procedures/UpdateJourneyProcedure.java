package xallawix.mod.journeyguide.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import xallawix.mod.journeyguide.JourneyGuideMod;
import xallawix.mod.journeyguide.configuration.JourneyGuideConfiguration;
import xallawix.mod.journeyguide.journey.Journey;
import xallawix.mod.journeyguide.journey.PlayerProgress;
import xallawix.mod.journeyguide.journey.Step;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;
import xallawix.mod.journeyguide.network.JourneySyncMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateJourneyProcedure {
	private static final int RESYNC_TICKS = 100;

	public static void execute(Entity entity) {
		if (!(entity instanceof ServerPlayer player))
			return;
		PlayerProgress progress = JourneyGuideModVariables.get(player);
		Map<String, Integer> stepProgress = new HashMap<>();
		boolean changed = false;
		for (Step step : Journey.steps()) {
			if (progress.completed.contains(step.id()))
				continue;
			int target = step.goal().target();
			int value = Math.min(step.goal().progress(player, progress), target);
			if (value >= target) {
				progress.completed.add(step.id());
				changed = true;
			} else {
				stepProgress.put(step.id(), value);
			}
		}

		Step current = currentStep(progress);
		String currentId = current == null ? "" : current.id();
		if (!currentId.equals(progress.currentStep)) {
			progress.resetStepTimer(currentId);
			changed = true;
		}
		if (current != null)
			FindStructurePointerProcedure.execute(player, current);

		if (!progress.welcomed) {
			progress.welcomed = true;
			changed = true;
			player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
					.append(Component.literal("Your guide to beating Minecraft is on the top left. Press ").withStyle(ChatFormatting.YELLOW))
					.append(Component.literal("J").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
					.append(Component.literal(" to open your journal and see every step.").withStyle(ChatFormatting.YELLOW)));
		}
		if (changed)
			JourneyGuideModVariables.save(player, progress);

		JourneySyncMessage message = toMessage(progress, stepProgress);
		int hash = message.contentHash();
		if (hash != progress.lastSyncHash || progress.ticksSinceSync >= RESYNC_TICKS) {
			progress.lastSyncHash = hash;
			progress.ticksSinceSync = 0;
			JourneyGuideMod.sendToPlayer(player, message);
		}
	}

	public static @Nullable Step currentStep(PlayerProgress progress) {
		for (Step step : Journey.steps()) {
			if (!progress.completed.contains(step.id()))
				return step;
		}
		return null;
	}

	private static JourneySyncMessage toMessage(PlayerProgress p, Map<String, Integer> stepProgress) {
		JourneySyncMessage.Pointer pointer = p.pointer == null ? null : new JourneySyncMessage.Pointer(p.pointer.pos(), p.pointer.dimension(), p.pointer.name());
		JourneySyncMessage.Build build = p.activeBuild == null ? null
				: new JourneySyncMessage.Build(p.activeBuild.blueprintId(), p.activeBuild.origin(), p.activeBuild.rotation(), p.activeBuild.dimension());
		return new JourneySyncMessage(new ArrayList<>(p.completed), new ArrayList<>(p.skipped), new ArrayList<>(p.completedBuilds), stepProgress, p.currentStep,
				p.ticksOnStep, p.extraTips, JourneyGuideConfiguration.EXTRA_TIP_MINUTES.get() * 60 * 20, JourneyGuideConfiguration.POINTER_MINUTES.get() * 60 * 20,
				pointer, build);
	}
}
