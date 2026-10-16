/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.backpack.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.backpack.BackpackMod;
import xallawix.mod.backpack.network.OpenBackpackMessage;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BackpackModKeyMappings {
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(BackpackMod.id("main"));
	public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.backpack.open_backpack", InputConstants.Type.KEYBOARD, InputConstants.KEY_B, CATEGORY);

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_BACKPACK);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		while (OPEN_BACKPACK.consumeClick()) {
			if (mc.player != null && mc.gui.screen() == null)
				BackpackMod.sendToServer(new OpenBackpackMessage());
		}
	}
}
