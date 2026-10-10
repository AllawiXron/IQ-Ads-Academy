package xallawix.mod.journeyguide.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.network.JourneyGuideModVariables;

@Mod.EventBusSubscriber
public class PlayerLeavesTheWorldProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		execute(event.getEntity());
	}

	public static void execute(Entity entity) {
		if (!(entity instanceof ServerPlayer player))
			return;
		JourneyGuideModVariables.save(player, JourneyGuideModVariables.get(player));
		JourneyGuideModVariables.forget(player);
	}
}
