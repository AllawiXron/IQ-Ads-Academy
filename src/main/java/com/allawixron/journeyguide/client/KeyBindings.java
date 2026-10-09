package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.JourneyGuide;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public final class KeyBindings {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(JourneyGuide.id("main"));

    public static final KeyMapping OPEN_JOURNAL = key("open_journal", InputConstants.KEY_J);
    public static final KeyMapping TOGGLE_TRACKER = key("toggle_tracker", InputConstants.KEY_H);
    public static final KeyMapping ROTATE_BUILD = key("rotate_build", InputConstants.KEY_R);
    public static final KeyMapping RAISE_BUILD = key("raise_build", InputConstants.KEY_UP);
    public static final KeyMapping LOWER_BUILD = key("lower_build", InputConstants.KEY_DOWN);
    public static final KeyMapping CONFIRM_BUILD = key("confirm_build", InputConstants.KEY_RETURN);
    public static final KeyMapping CANCEL_BUILD = key("cancel_build", InputConstants.KEY_BACKSPACE);

    public static final KeyMapping[] ALL = {
            OPEN_JOURNAL, TOGGLE_TRACKER, ROTATE_BUILD, RAISE_BUILD, LOWER_BUILD, CONFIRM_BUILD, CANCEL_BUILD
    };

    private KeyBindings() {}

    private static KeyMapping key(String name, int key) {
        return new KeyMapping("key." + JourneyGuide.MODID + "." + name, InputConstants.Type.KEYBOARD, key, CATEGORY);
    }
}
