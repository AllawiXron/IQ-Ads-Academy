package xallawix.mod.journeyguide.procedures;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.client.ClientState;
import xallawix.mod.journeyguide.client.Holograms;
import xallawix.mod.journeyguide.client.PlacementMode;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientLeavesTheWorldProcedure {
	@SubscribeEvent
	public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		execute();
	}

	public static void execute() {
		ClientState.reset();
		PlacementMode.cancel();
		Holograms.reset();
	}
}
