package com.allawixron.journeyguide.journey;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Something the player has to achieve to finish a step. Goals are evaluated on the server
 * and report a number between 0 and {@link #target()} so the tracker can show a progress bar.
 */
public interface Goal {
    int target();

    int progress(ServerPlayer player, PlayerProgress data);

    /** Collect items. Counts what is in the inventory, or everything ever picked up and crafted, whichever is higher. */
    static Goal obtain(int count, Item... items) {
        return new Obtain(() -> List.of(items), count);
    }

    static Goal obtain(int count, TagKey<Item> tag) {
        return new Obtain(() -> {
            List<Item> items = new ArrayList<>();
            for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag))
                items.add(holder.value());
            return items;
        }, count);
    }

    /** Items you made yourself: crafted, or smelted and taken out of a furnace. Items found in chests don't count. */
    static Goal crafted(int count, Item... items) {
        return new Crafted(List.of(items), count);
    }

    static Goal advancement(String path) {
        return new Advancement(Identifier.withDefaultNamespace(path));
    }

    static Goal customStat(Identifier stat, int count) {
        return new CustomStat(stat, count);
    }

    static Goal build(String blueprintId) {
        return new Build(blueprintId);
    }

    static Goal allOf(Goal... goals) {
        return new AllOf(List.of(goals));
    }

    static Goal anyOf(Goal... goals) {
        return new AnyOf(List.of(goals));
    }

    interface ItemList {
        List<Item> get();
    }

    final class Obtain implements Goal {
        private final ItemList source;
        private final int count;
        private List<Item> items;

        Obtain(ItemList source, int count) {
            this.source = source;
            this.count = count;
        }

        private List<Item> items() {
            if (items == null || items.isEmpty())
                items = source.get();
            return items;
        }

        @Override
        public int target() {
            return count;
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            List<Item> wanted = items();
            int inInventory = 0;
            Inventory inv = player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (!stack.isEmpty() && wanted.contains(stack.getItem()))
                    inInventory += stack.getCount();
            }

            int everObtained = 0;
            for (Item item : wanted) {
                everObtained += player.getStats().getValue(Stats.ITEM_PICKED_UP, item);
                everObtained += player.getStats().getValue(Stats.ITEM_CRAFTED, item);
            }
            return Math.max(inInventory, everObtained);
        }
    }

    record Crafted(List<Item> items, int count) implements Goal {
        @Override
        public int target() {
            return count;
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            int made = 0;
            for (Item item : items)
                made += player.getStats().getValue(Stats.ITEM_CRAFTED, item);
            return made;
        }
    }

    record Advancement(Identifier id) implements Goal {
        @Override
        public int target() {
            return 1;
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            AdvancementHolder holder = player.level().getServer().getAdvancements().get(id);
            if (holder == null)
                return 0;
            return player.getAdvancements().getOrStartProgress(holder).isDone() ? 1 : 0;
        }
    }

    record CustomStat(Identifier stat, int count) implements Goal {
        @Override
        public int target() {
            return count;
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            return player.getStats().getValue(Stats.CUSTOM, stat);
        }
    }

    record Build(String blueprintId) implements Goal {
        @Override
        public int target() {
            return 1;
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            return data.completedBuilds.contains(blueprintId) ? 1 : 0;
        }
    }

    record AllOf(List<Goal> goals) implements Goal {
        @Override
        public int target() {
            int total = 0;
            for (Goal goal : goals)
                total += goal.target();
            return total;
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            int total = 0;
            for (Goal goal : goals)
                total += Math.min(goal.progress(player, data), goal.target());
            return total;
        }
    }

    /** Finished as soon as any of the goals is finished. Progress shows the closest one. */
    record AnyOf(List<Goal> goals) implements Goal {
        @Override
        public int target() {
            return goals.getFirst().target();
        }

        @Override
        public int progress(ServerPlayer player, PlayerProgress data) {
            int target = target();
            int best = 0;
            for (Goal goal : goals) {
                int p = Math.min(goal.progress(player, data), goal.target());
                if (p >= goal.target())
                    return target;
                best = Math.max(best, p * target / goal.target());
            }
            return Math.min(best, target - 1);
        }
    }
}
