/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.backpack.init;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.backpack.client.renderer.BackpackLayer;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BackpackModRenderLayers {
	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (PlayerModelType type : event.getModelTypes()) {
			AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(type);
			if (renderer != null)
				renderer.addLayer(new BackpackLayer(renderer));
		}
	}
}
