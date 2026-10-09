package com.allawixron.journeyguide.journey;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * One step of the journey.
 *
 * @param summary   one short line telling the player what to do (shown on the tracker)
 * @param why       why this step matters for beating the game
 * @param tips      how-to tips; the first is always shown, the rest unlock over time or on request
 * @param locate    a structure the pointer can lead to when the player is stuck
 * @param bestY     the best height to search at (shown on the tracker)
 * @param blueprint a blueprint that completes or helps with this step
 */
public record Step(
        String id,
        Chapter chapter,
        String title,
        String summary,
        String why,
        List<String> tips,
        Item icon,
        Goal goal,
        @Nullable Locate locate,
        @Nullable Integer bestY,
        @Nullable String blueprint
) {
    public ItemStack iconStack() {
        return new ItemStack(icon);
    }

    public record Locate(TagKey<Structure> structures, ResourceKey<Level> dimension, String name) {}
}
