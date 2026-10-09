package com.allawixron.journeyguide;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue EXTRA_TIP_MINUTES = BUILDER
            .comment("Minutes spent on one step before the next tip unlocks by itself.")
            .defineInRange("extraTipMinutes", 6, 1, 600);

    public static final ForgeConfigSpec.IntValue POINTER_MINUTES = BUILDER
            .comment("Minutes spent on a 'find a place' step (village, fortress, stronghold)",
                     "before a pointer arrow unlocks to show the way.")
            .defineInRange("pointerMinutes", 15, 0, 600);

    public static final ForgeConfigSpec.IntValue POINTER_SEARCH_CHUNKS = BUILDER
            .comment("How far (in chunks) to search for structures when the pointer unlocks.")
            .defineInRange("pointerSearchChunks", 100, 8, 400);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
