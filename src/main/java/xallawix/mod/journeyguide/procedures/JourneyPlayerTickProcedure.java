package xallawix.mod.journeyguide.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.journey.PlayerProgress;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;

@Mod.EventBusSubscriber
public class JourneyPlayerTickProcedure {
	private static final int CHECK_INTERVAL = 20;
	private static final int BUILD_CHECK_INTERVAL = 40;

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
		execute(event.player());
	}

	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof ServerPlayer _player))
			return;
		if (_player.tickCount % CHECK_INTERVAL != 0)
			return;
		PlayerProgress progress = JourneyGuideModVariables.get(_player);
		progress.ticksOnStep += CHECK_INTERVAL;
		progress.ticksSinceSync += CHECK_INTERVAL;
		if (progress.activeBuild != null && _player.tickCount % BUILD_CHECK_INTERVAL == 0)
			CheckBuildProcedure.execute(_player);
		UpdateJourneyProcedure.execute(_player);
	}
}
