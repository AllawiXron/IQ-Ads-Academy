package xallawix.mod.journeyguide.journey;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jspecify.annotations.Nullable;

import java.util.List;

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
