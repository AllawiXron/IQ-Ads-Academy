package com.allawixron.journeyguide.client;

import net.minecraftforge.common.ForgeConfigSpec;

/** Settings that only matter on your own screen. Saved in config/journeyguide-client.toml. */
public final class ClientConfig {
    public enum TrackerMode {
        COMPACT, DETAILED, HIDDEN;

        TrackerMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public enum Corner { TOP_LEFT, TOP_RIGHT }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.EnumValue<TrackerMode> TRACKER_MODE = BUILDER
            .comment("How much the tracker shows: COMPACT (one small box), DETAILED (tips and more) or HIDDEN.",
                     "The H key cycles through these in game.")
            .defineEnum("trackerMode", TrackerMode.COMPACT);

    private static final ForgeConfigSpec.EnumValue<Corner> TRACKER_CORNER = BUILDER
            .comment("Which corner of the screen the tracker sits in.")
            .defineEnum("trackerCorner", Corner.TOP_LEFT);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ClientConfig() {}

    public static TrackerMode mode() {
        try {
            return TRACKER_MODE.get();
        } catch (IllegalStateException notLoadedYet) {
            return TrackerMode.COMPACT;
        }
    }

    public static Corner corner() {
        try {
            return TRACKER_CORNER.get();
        } catch (IllegalStateException notLoadedYet) {
            return Corner.TOP_LEFT;
        }
    }

    public static TrackerMode cycleMode() {
        TrackerMode next = mode().next();
        try {
            TRACKER_MODE.set(next);
            TRACKER_MODE.save();
        } catch (IllegalStateException notLoadedYet) {
            // can't save before the config is loaded; nothing to do
        }
        return next;
    }
}
