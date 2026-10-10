/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.journeyguide.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.journeyguide.JourneyGuideMod;
import xallawix.mod.journeyguide.client.PlacementMode;
import xallawix.mod.journeyguide.client.gui.JournalScreen;
import xallawix.mod.journeyguide.configuration.JourneyGuideClientConfiguration;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class JourneyGuideModKeyMappings {
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(JourneyGuideMod.id("main"));
	public static final KeyMapping OPEN_JOURNAL = new KeyMapping("key.journeyguide.open_journal", InputConstants.Type.KEYBOARD, InputConstants.KEY_J, CATEGORY);
	public static final KeyMapping TOGGLE_TRACKER = new KeyMapping("key.journeyguide.toggle_tracker", InputConstants.Type.KEYBOARD, InputConstants.KEY_H, CATEGORY);
	public static final KeyMapping ROTATE_BUILD = new KeyMapping("key.journeyguide.rotate_build", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, CATEGORY);
	public static final KeyMapping RAISE_BUILD = new KeyMapping("key.journeyguide.raise_build", InputConstants.Type.KEYBOARD, InputConstants.KEY_UP, CATEGORY);
	public static final KeyMapping LOWER_BUILD = new KeyMapping("key.journeyguide.lower_build", InputConstants.Type.KEYBOARD, InputConstants.KEY_DOWN, CATEGORY);
	public static final KeyMapping CONFIRM_BUILD = new KeyMapping("key.journeyguide.confirm_build", InputConstants.Type.KEYBOARD, InputConstants.KEY_RETURN, CATEGORY);
	public static final KeyMapping CANCEL_BUILD = new KeyMapping("key.journeyguide.cancel_build", InputConstants.Type.KEYBOARD, InputConstants.KEY_BACKSPACE, CATEGORY);

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_JOURNAL);
		event.register(TOGGLE_TRACKER);
		event.register(ROTATE_BUILD);
		event.register(RAISE_BUILD);
		event.register(LOWER_BUILD);
		event.register(CONFIRM_BUILD);
		event.register(CANCEL_BUILD);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null)
			return;
		while (OPEN_JOURNAL.consumeClick()) {
			if (mc.gui.screen() == null)
				mc.gui.setScreen(new JournalScreen());
		}
		while (TOGGLE_TRACKER.consumeClick()) {
			JourneyGuideClientConfiguration.TrackerMode mode = JourneyGuideClientConfiguration.cycleMode();
			String name = switch (mode) {
				case COMPACT -> "compact";
				case DETAILED -> "detailed (with tips)";
				case HIDDEN -> "hidden (press H to bring it back)";
			};
			mc.player.sendOverlayMessage(Component.literal("Tracker: " + name).withStyle(ChatFormatting.GOLD));
		}
		if (PlacementMode.active()) {
			PlacementMode.tick();
		} else {
			ROTATE_BUILD.consumeClick();
			RAISE_BUILD.consumeClick();
			LOWER_BUILD.consumeClick();
			CONFIRM_BUILD.consumeClick();
			CANCEL_BUILD.consumeClick();
		}
	}
}
