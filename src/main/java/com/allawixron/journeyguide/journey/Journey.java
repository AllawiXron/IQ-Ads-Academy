package com.allawixron.journeyguide.journey;

import com.allawixron.journeyguide.JourneyGuide;
import net.minecraft.core.registries.Registries;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The full journey from the first tree to the Ender Dragon. */
public final class Journey {
    public static final TagKey<Structure> FORTRESS = TagKey.create(Registries.STRUCTURE, JourneyGuide.id("fortress"));

    private static List<Step> steps;
    private static Map<String, Step> byId;

    private Journey() {}

    public static List<Step> steps() {
        if (steps == null)
            build();
        return steps;
    }

    public static @Nullable Step get(String id) {
        if (byId == null)
            build();
        return byId.get(id);
    }

    public static int indexOf(Step step) {
        return steps().indexOf(step);
    }

    public static List<Step> inChapter(Chapter chapter) {
        List<Step> result = new ArrayList<>();
        for (Step step : steps())
            if (step.chapter() == chapter)
                result.add(step);
        return result;
    }

    private static synchronized void build() {
        if (steps != null)
            return;
        List<Step> list = new ArrayList<>();
        var b = new Builder(list);

        // ---------------------------------------------------------------- The First Day
        b.chapter(Chapter.FIRST_DAY);
        b.step("punch_tree", "Punch a tree", "Collect 6 logs", Items.OAK_LOG,
                Goal.obtain(6, ItemTags.LOGS),
                "Wood is the start of everything: tools, crafting table, shelter, torches.",
                "Walk up to a tree and hold left click on the trunk until the log drops, then walk over it.",
                "Take logs from a few trees. Leave the leaves; they disappear by themselves.",
                "Any kind of wood works: oak, birch, spruce, poplar...");
        b.step("crafting_table", "Crafting table", "Craft a crafting table", Items.CRAFTING_TABLE,
                Goal.obtain(1, Items.CRAFTING_TABLE),
                "The crafting table gives you the 3x3 grid you need for tools.",
                "Open your inventory (E), put a log in the small 2x2 grid to get 4 planks.",
                "Fill all four squares of the 2x2 grid with planks to get a crafting table, then place it on the ground.");
        b.step("wooden_pickaxe", "First pickaxe", "Craft a wooden pickaxe", Items.WOODEN_PICKAXE,
                Goal.obtain(1, Items.WOODEN_PICKAXE),
                "You need a pickaxe to mine stone. Wood is weak, so you'll only use this one briefly.",
                "Make sticks: two planks stacked vertically in a crafting grid give 4 sticks.",
                "Pickaxe: 3 planks across the top row of the crafting table, 2 sticks down the middle.");
        b.step("cobblestone", "Mine some stone", "Collect 20 cobblestone", Items.COBBLESTONE,
                Goal.obtain(20, ItemTags.STONE_TOOL_MATERIALS),
                "Stone tools are much better than wood, and cobblestone is great for building.",
                "Look for grey stone on hillsides or dig down a few blocks (never straight down under your feet!).",
                "Dig in a staircase pattern so you can always walk back up.");
        b.step("stone_tools", "Stone tools", "Craft a stone pickaxe, sword and axe", Items.STONE_PICKAXE,
                Goal.allOf(Goal.obtain(1, Items.STONE_PICKAXE), Goal.obtain(1, Items.STONE_SWORD), Goal.obtain(1, Items.STONE_AXE)),
                "A sword keeps you safe at night, the axe chops wood fast and the pickaxe can mine iron.",
                "Same shapes as wood tools, just use cobblestone instead of planks.",
                "Sword: 2 cobblestone stacked with a stick below. Axe: 3 cobblestone in an L shape with 2 sticks.");
        b.step("coal_torches", "Light the way", "Craft 8 torches", Items.TORCH,
                Goal.obtain(8, Items.TORCH),
                "Monsters spawn in the dark. Torches keep your home safe and help you find your way in caves.",
                "Coal ore is stone with black spots, easy to find on cliffs and in caves. Mine it with any pickaxe.",
                "No coal? Smelt logs in a furnace to get charcoal; it works the same.",
                "Torch: 1 coal on top of 1 stick gives 4 torches.");
        b.step("food", "Find food", "Collect 6 pieces of food", Items.APPLE,
                Goal.obtain(6, Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT, Items.COD, Items.SALMON,
                        Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.COOKED_MUTTON, Items.COOKED_CHICKEN, Items.COOKED_RABBIT,
                        Items.COOKED_COD, Items.COOKED_SALMON, Items.BREAD, Items.APPLE, Items.CARROT, Items.POTATO,
                        Items.BAKED_POTATO, Items.SWEET_BERRIES, Items.MELON_SLICE),
                "When your hunger bar runs low you stop healing and can't sprint.",
                "Cows, pigs, sheep and chickens drop meat. Oak leaves sometimes drop apples.",
                "Sweet berry bushes in taiga forests are free food (watch out, they scratch).",
                "Raw meat is fine for now; cook it later for much more hunger and saturation.");
        b.step("first_shelter", "Night shelter", "Build the First Night Shelter", Items.OAK_DOOR,
                Goal.build("starter_hut"),
                "The first night is the most dangerous: zombies, skeletons and creepers come out in the dark.",
                "Open the journal (J) > Builds > First Night Shelter and press Place. A hologram shows where every block goes.",
                "Build layer by layer. The tracker tells you which blocks you still need.",
                "Built your own shelter already? Use 'Skip step' in the journal.");
        b.step("sleep", "Sleep in a bed", "Sleep through one night", Items.BED.pick(DyeColor.WHITE),
                Goal.customStat(Stats.SLEEP_IN_BED, 1),
                "Sleeping skips the night and sets your respawn point at the bed.",
                "A bed needs 3 wool + 3 planks. Shear or kill 3 sheep to get wool (all the same color).",
                "You can only sleep at night or during a thunderstorm, and no monsters can be close by.");
        b.blueprint("starter_hut", "first_shelter");

        // ---------------------------------------------------------------- Settling In
        b.chapter(Chapter.SETTLING_IN);
        b.step("furnace", "Build a furnace", "Craft a furnace", Items.FURNACE,
                Goal.obtain(1, Items.FURNACE),
                "Furnaces cook food and smelt ores into ingots.",
                "Put 8 cobblestone around the edge of the crafting table grid, leaving the middle empty.");
        b.step("cooked_food", "Cook a meal", "Cook 5 pieces of food", Items.COOKED_BEEF,
                Goal.obtain(5, Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.COOKED_MUTTON, Items.COOKED_CHICKEN,
                        Items.COOKED_RABBIT, Items.COOKED_COD, Items.COOKED_SALMON, Items.BAKED_POTATO, Items.BREAD),
                "Cooked meat fills your hunger bar much more than raw meat.",
                "Raw food goes in the top slot of the furnace, fuel (coal, charcoal, planks) in the bottom.",
                "Bread works too: 3 wheat in a row on the crafting table.");
        b.step("cozy_cabin", "A real home", "Build the Cozy Cabin", Items.SPRUCE_DOOR,
                Goal.build("cozy_cabin"),
                "A proper base keeps your stuff safe and gives you a place to come back to.",
                "Open the journal (J) > Builds > Cozy Cabin and press Place. Look at a spot and press Enter to confirm.",
                "You'll need glass for the windows: smelt sand in a furnace.",
                "Place torches inside and around the cabin so no monsters spawn nearby.",
                "Building your own house instead? That's great; use 'Skip step' when it's done.");
        b.step("storage", "Get organized", "Craft 4 chests", Items.CHEST,
                Goal.obtain(4, Items.CHEST),
                "You'll collect a lot of stuff. Chests keep it safe when you die.",
                "Chest: 8 planks around the edge of the crafting grid.",
                "Two chests side by side make one big double chest.");
        b.step("farm", "Start a farm", "Build the Wheat Farm or collect 16 wheat", Items.WHEAT,
                Goal.anyOf(Goal.obtain(16, Items.WHEAT), Goal.build("wheat_farm")),
                "A farm means you never run out of food, and wheat lets you breed cows and sheep.",
                "Break tall grass to find seeds. Till dirt with a hoe next to water, then plant the seeds.",
                "One water block hydrates farmland up to 4 blocks away in every direction.",
                "Wheat is ready when it's tall and golden brown.");
        b.step("iron", "Smelt iron", "Smelt iron ingots", Items.IRON_INGOT,
                Goal.advancement("story/smelt_iron"),
                "Iron makes far better tools and armor, plus buckets and shields.",
                "Iron ore is stone with orange-beige spots. It's common in caves and on mountains.",
                "Mine it with a stone pickaxe or better, then smelt the raw iron in your furnace.");
        b.step("iron_pickaxe", "Iron pickaxe", "Craft an iron pickaxe", Items.IRON_PICKAXE,
                Goal.obtain(1, Items.IRON_PICKAXE),
                "Only an iron pickaxe (or better) can mine diamonds.",
                "3 iron ingots across the top, 2 sticks down the middle.");
        b.step("bucket_shield", "Bucket & shield", "Craft a bucket and a shield", Items.SHIELD,
                Goal.allOf(Goal.obtain(1, Items.BUCKET, Items.WATER_BUCKET, Items.LAVA_BUCKET), Goal.obtain(1, Items.SHIELD)),
                "A water bucket saves you from falls and lava. A shield blocks skeleton arrows and creeper blasts.",
                "Bucket: 3 iron ingots in a V shape.",
                "Shield: 6 planks with 1 iron ingot in the top middle. Hold right click to block.");
        b.step("iron_armor", "Suit up", "Make a full set of iron armor", Items.IRON_CHESTPLATE,
                Goal.allOf(Goal.obtain(1, Items.IRON_HELMET), Goal.obtain(1, Items.IRON_CHESTPLATE),
                        Goal.obtain(1, Items.IRON_LEGGINGS), Goal.obtain(1, Items.IRON_BOOTS)),
                "Full iron armor makes caves and the Nether far less scary.",
                "The full set takes 24 iron ingots: helmet 5, chestplate 8, leggings 7, boots 4.",
                "Caves with lots of exposed stone are the fastest place to find iron.");
        b.blueprint("cozy_cabin", "cozy_cabin");
        b.blueprint("wheat_farm", "farm");

        // ---------------------------------------------------------------- Going Deeper
        b.chapter(Chapter.GOING_DEEPER);
        b.step("diamonds", "Find diamonds", "Mine your first diamonds", Items.DIAMOND,
                Goal.advancement("story/mine_diamond"),
                "Diamond tools are the best you'll get before the Nether, and you need diamonds for an enchanting table.",
                "Diamonds are most common deep down, around Y = -58. Press F3 to see your Y level, or check the tracker.",
                "Bring torches, food, a water bucket and lots of cobblestone. Lava is common at that depth.",
                "Big caves are faster than digging, but tunnels are safer. Always watch for lava.",
                "Mine diamond ore with an iron pickaxe or better, or it drops nothing.")
                .bestY(-58);
        b.step("diamond_pickaxe", "Diamond pickaxe", "Craft a diamond pickaxe", Items.DIAMOND_PICKAXE,
                Goal.obtain(1, Items.DIAMOND_PICKAXE),
                "Only a diamond pickaxe can mine obsidian, which you need for the Nether portal and enchanting.",
                "3 diamonds across the top, 2 sticks down the middle.");
        b.step("obsidian", "Get obsidian", "Collect 14 obsidian", Items.OBSIDIAN,
                Goal.obtain(14, Items.OBSIDIAN),
                "10 obsidian builds a Nether portal and 4 more go into an enchanting table.",
                "Obsidian forms when water touches still lava. Find a lava pool and pour water on it.",
                "Mine it with the diamond pickaxe; it takes about 10 seconds per block.",
                "Careful: there may be more lava under the obsidian. Put a block down before you step on it.");
        b.step("enchanting_table", "Enchanting table", "Craft an enchanting table", Items.ENCHANTING_TABLE,
                Goal.obtain(1, Items.ENCHANTING_TABLE),
                "Enchantments make your gear much stronger. You'll want them for the Nether and the dragon.",
                "Recipe: 1 book on top, 2 diamonds on the sides, 4 obsidian below.",
                "Book: 3 paper (sugar cane) + 1 leather (cows).");
        b.step("enchanting_room", "Enchanting room", "Build the Enchanting Room or craft 15 bookshelves", Items.BOOKSHELF,
                Goal.anyOf(Goal.obtain(15, Items.BOOKSHELF), Goal.build("enchanting_room")),
                "15 bookshelves around the table unlock the strongest (level 30) enchantments.",
                "Bookshelf: 6 planks + 3 books. 15 shelves need 45 books, so start a sugar cane farm and keep cows.",
                "Bookshelves must be exactly one block away from the table with nothing in between.");
        b.step("enchant", "Enchant gear", "Enchant an item", Items.ENCHANTED_BOOK,
                Goal.advancement("story/enchant_item"),
                "Enchanting your pickaxe, sword and armor is the best way to prepare for the Nether.",
                "Enchanting costs levels and lapis lazuli (blue ore found near diamond depth).",
                "Good picks: Protection on armor, Sharpness on the sword, Efficiency on the pickaxe.");
        b.blueprint("enchanting_room", "enchanting_room");

        // ---------------------------------------------------------------- The Nether
        b.chapter(Chapter.THE_NETHER);
        b.step("flint_and_steel", "Flint and steel", "Craft flint and steel", Items.FLINT_AND_STEEL,
                Goal.obtain(1, Items.FLINT_AND_STEEL),
                "You need fire to light the Nether portal.",
                "Dig gravel until it drops flint, then combine 1 flint + 1 iron ingot.");
        b.step("portal", "Nether portal", "Build the Nether Portal and step in", Items.OBSIDIAN,
                Goal.anyOf(Goal.build("nether_portal"), Goal.advancement("story/enter_the_nether")),
                "The Nether has blaze rods, which you need for Eyes of Ender.",
                "Open the journal (J) > Builds > Nether Portal for the obsidian frame (4 wide, 5 tall, corners optional).",
                "Light the inside of the frame with flint and steel.");
        b.step("enter_nether", "Into the Nether", "Enter the Nether", Items.NETHERRACK,
                Goal.advancement("story/enter_the_nether"),
                "This is where the game gets serious: lava everywhere, ghasts and piglins.",
                "Wear at least one piece of gold armor so piglins don't attack you.",
                "Bring blocks, food, a bow, and flint and steel in case a ghast blows out your portal.",
                "Beds explode in the Nether! Don't try to sleep there.");
        b.step("fortress", "Find a fortress", "Find a Nether fortress", Items.NETHER_BRICKS,
                Goal.advancement("nether/find_fortress"),
                "Nether fortresses are the only place where blazes spawn.",
                "Fortresses are big dark-red brick structures. Explore along the X axis (east or west); they're usually lined up that way.",
                "Build a safe path with cobblestone. Ghast fireballs can't break it.")
                .locate(FORTRESS, Level.NETHER, "Nether fortress");
        b.step("blaze_rods", "Blaze rods", "Collect 7 blaze rods", Items.BLAZE_ROD,
                Goal.obtain(7, Items.BLAZE_ROD),
                "Blaze rods are crafted into blaze powder, the main ingredient of Eyes of Ender.",
                "Blazes spawn near blaze spawners in fortresses. They float and shoot fireballs.",
                "Fight them from behind a corner so they have to come close. A shield blocks their fireballs.",
                "Snowballs hurt blazes, and Fire Resistance potions make the fight much easier.");
        b.blueprint("nether_portal", "portal");

        // ---------------------------------------------------------------- Eyes of Ender
        b.chapter(Chapter.EYES_OF_ENDER);
        b.step("ender_pearls", "Ender pearls", "Collect 12 ender pearls", Items.ENDER_PEARL,
                Goal.obtain(12, Items.ENDER_PEARL),
                "Each Eye of Ender is 1 ender pearl + 1 blaze powder.",
                "Endermen drop pearls. They're tall, black and only attack if you look at their face.",
                "Fight endermen while standing under a 2-block-high ceiling; they can't reach you there.",
                "Trading with piglins (throw them gold ingots) or cleric villagers also gives pearls.");
        b.step("eyes", "Eyes of Ender", "Craft 12 Eyes of Ender", Items.ENDER_EYE,
                Goal.obtain(12, Items.ENDER_EYE),
                "Eyes of Ender lead you to the stronghold and activate the End portal.",
                "Put a blaze rod in the crafting grid to get 2 blaze powder.",
                "Eye of Ender: 1 ender pearl + 1 blaze powder (shapeless).",
                "12 is the safe number: some eyes break when thrown and the portal needs up to 12.");
        b.step("stronghold", "Find stronghold", "Follow the Eyes of Ender to the stronghold", Items.ENDER_EYE,
                Goal.advancement("story/follow_ender_eye"),
                "The End portal is hidden deep inside the stronghold.",
                "Throw an eye (right click) and walk in the direction it flies. Pick it up again if it drops.",
                "When the eye flies down into the ground, dig down there. The stronghold is underground.",
                "Inside, look for the portal room: lava pool, stairs, and a frame of 12 portal blocks.")
                .locate(StructureTags.EYE_OF_ENDER_LOCATED, Level.OVERWORLD, "Stronghold");
        b.step("prepare", "Prepare to fight", "Get a bow and 64 arrows", Items.BOW,
                Goal.allOf(Goal.obtain(1, Items.BOW), Goal.obtain(64, Items.ARROW)),
                "The dragon heals from end crystals on top of the tall obsidian towers. You have to shoot them.",
                "Bow: 3 sticks + 3 string (from spiders). Arrows: flint + stick + feather.",
                "Also bring: lots of blocks, food, a water bucket, a pumpkin for your head (endermen won't get angry).",
                "Set your spawn point near the stronghold by sleeping in a bed there.");
        b.step("enter_end", "Open the portal", "Fill the End portal and jump in", Items.END_PORTAL_FRAME,
                Goal.advancement("story/enter_the_end"),
                "There's no way back until the dragon is dead (or you die), so be ready.",
                "Right click each empty portal frame with an Eye of Ender until all 12 have eyes.",
                "Don't break the portal frame blocks; they can't be picked up again.");

        // ---------------------------------------------------------------- The End
        b.chapter(Chapter.THE_END);
        b.step("kill_dragon", "Slay the dragon", "Kill the Ender Dragon", Items.DRAGON_HEAD,
                Goal.advancement("end/kill_dragon"),
                "This is it: beat the dragon and you've finished Minecraft!",
                "First shoot the end crystals on top of the towers. Caged ones need you to pillar up and break the cage.",
                "When the dragon lands on the portal in the middle, hit it with your sword as much as you can.",
                "Don't look endermen in the eyes. Wear a pumpkin or look at the floor.",
                "Never use a bed in the End: it explodes. Do bring a water bucket for falls.");

        steps = Collections.unmodifiableList(list);
        Map<String, Step> map = new LinkedHashMap<>();
        for (Step step : list)
            map.put(step.id(), step);
        byId = map;
    }

    /** Small helper so the step list above stays readable. */
    private static final class Builder {
        private final List<Step> list;
        private Chapter chapter;

        Builder(List<Step> list) {
            this.list = list;
        }

        void chapter(Chapter chapter) {
            this.chapter = chapter;
        }

        StepRef step(String id, String title, String summary, Item icon, Goal goal, String why, String... tips) {
            list.add(new Step(id, chapter, title, summary, why, List.of(tips), icon, goal, null, null, null));
            return new StepRef(list, list.size() - 1);
        }

        void blueprint(String blueprintId, String stepId) {
            for (int i = 0; i < list.size(); i++) {
                Step s = list.get(i);
                if (s.id().equals(stepId))
                    list.set(i, new Step(s.id(), s.chapter(), s.title(), s.summary(), s.why(), s.tips(), s.icon(), s.goal(), s.locate(), s.bestY(), blueprintId));
            }
        }
    }

    private record StepRef(List<Step> list, int index) {
        StepRef bestY(int y) {
            Step s = list.get(index);
            list.set(index, new Step(s.id(), s.chapter(), s.title(), s.summary(), s.why(), s.tips(), s.icon(), s.goal(), s.locate(), y, s.blueprint()));
            return this;
        }

        StepRef locate(TagKey<Structure> tag, net.minecraft.resources.ResourceKey<Level> dimension, String name) {
            Step s = list.get(index);
            list.set(index, new Step(s.id(), s.chapter(), s.title(), s.summary(), s.why(), s.tips(), s.icon(), s.goal(),
                    new Step.Locate(tag, dimension, name), s.bestY(), s.blueprint()));
            return this;
        }
    }
}
