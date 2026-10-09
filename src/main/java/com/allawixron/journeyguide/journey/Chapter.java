package com.allawixron.journeyguide.journey;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;

public enum Chapter {
    FIRST_DAY("The First Day", "Get wood, tools and a roof over your head before the first night."),
    SETTLING_IN("Settling In", "Turn your shelter into a real home with food, storage and iron gear."),
    GOING_DEEPER("Going Deeper", "Dig for diamonds, collect obsidian and start enchanting."),
    THE_NETHER("The Nether", "Build a portal, survive the Nether and take blaze rods from a fortress."),
    EYES_OF_ENDER("Eyes of Ender", "Craft Eyes of Ender and follow them to the stronghold."),
    THE_END("The End", "Defeat the Ender Dragon and finish the game.");

    public final String title;
    public final String description;

    Chapter(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ItemStack icon() {
        Item item = switch (this) {
            case FIRST_DAY -> Items.OAK_SAPLING;
            case SETTLING_IN -> Items.BED.pick(DyeColor.RED);
            case GOING_DEEPER -> Items.DIAMOND_PICKAXE;
            case THE_NETHER -> Items.NETHERRACK;
            case EYES_OF_ENDER -> Items.ENDER_EYE;
            case THE_END -> Items.DRAGON_HEAD;
        };
        return new ItemStack(item);
    }
}
