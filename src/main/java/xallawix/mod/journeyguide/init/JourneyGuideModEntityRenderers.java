package xallawix.mod.journeyguide.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.client.renderer.HologramAnchorRenderer;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class JourneyGuideModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(JourneyGuideModEntities.HOLOGRAM_ANCHOR.get(), HologramAnchorRenderer::new);
	}
}
