package xallawix.mod.journeyguide.procedures;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.client.BuildView;
import xallawix.mod.journeyguide.client.ClientState;
import xallawix.mod.journeyguide.client.Holograms;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class JourneyClientTickProcedure {
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
		execute();
	}

	public static void execute() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null)
			return;
		ClientState.tick();
		BuildView.tick();
		Holograms.tick();
	}
}
