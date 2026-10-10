package xallawix.mod.journeyguide.build;

import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Blueprints {
    private static Map<String, Blueprint> all;

    private Blueprints() {}

    public static List<Blueprint> all() {
        if (all == null)
            init();
        return List.copyOf(all.values());
    }

    public static @Nullable Blueprint get(String id) {
        if (all == null)
            init();
        return all.get(id);
    }

    private static synchronized void init() {
        if (all != null)
            return;
        Map<String, Blueprint> map = new LinkedHashMap<>();
        for (Blueprint bp : List.of(starterHut(), cozyCabin(), wheatFarm(), animalPen(), enchantingRoom(), netherPortal()))
            map.put(bp.id, bp);
        all = Collections.unmodifiableMap(map);
    }

    private static BlockState door(Block door, DoubleBlockHalf half) {
        return door.defaultBlockState().setValue(DoorBlock.FACING, Direction.NORTH).setValue(DoorBlock.HALF, half);
    }

    private static BlockState bed(Block bed, BedPart part) {
        return bed.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH).setValue(BedBlock.PART, part);
    }

    private static BlockState stairs(Block stairs, Direction facing) {
        return stairs.defaultBlockState().setValue(StairBlock.FACING, facing);
    }

    private static Blueprint starterHut() {
        return Blueprint.builder("starter_hut", "First Night Shelter", Items.OAK_DOOR)
                .description("A small wooden hut to survive your first night. Quick to build with just wood, "
                        + "and it has room for a crafting table, a torch and a bed later.")
                .key('L', Blocks.OAK_LOG)
                .key('P', Blocks.OAK_PLANKS)
                .key('D', door(Blocks.OAK_DOOR, DoubleBlockHalf.LOWER))
                .key('d', door(Blocks.OAK_DOOR, DoubleBlockHalf.UPPER))
                .key('T', Blocks.CRAFTING_TABLE)
                .key('t', Blocks.TORCH)
                .layer(
                        "LPPPL",
                        "PT..P",
                        "P...P",
                        "Pt..P",
                        "LPDPL")
                .layer(
                        "LPPPL",
                        "P...P",
                        "P...P",
                        "P...P",
                        "LPdPL")
                .layer(
                        "PPPPP",
                        "PPPPP",
                        "PPPPP",
                        "PPPPP",
                        "PPPPP")
                .build();
    }

    private static Blueprint cozyCabin() {
        return Blueprint.builder("cozy_cabin", "Cozy Cabin", Items.SPRUCE_DOOR)
                .description("A proper rustic home with a cobblestone floor, log frame, glass windows and a pitched roof. "
                        + "Comes with a bed, furnace, crafting table and a double chest.")
                .sink(1)
                .key('C', Blocks.COBBLESTONE)
                .key('L', Blocks.OAK_LOG)
                .key('P', Blocks.OAK_PLANKS)
                .key('G', Blocks.GLASS)
                .key('D', door(Blocks.OAK_DOOR, DoubleBlockHalf.LOWER))
                .key('d', door(Blocks.OAK_DOOR, DoubleBlockHalf.UPPER))
                .key('B', bed(Blocks.BED.pick(DyeColor.RED), BedPart.HEAD))
                .key('b', bed(Blocks.BED.pick(DyeColor.RED), BedPart.FOOT))
                .key('F', Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.SOUTH))
                .key('T', Blocks.CRAFTING_TABLE)
                .key('H', Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.WEST))
                .key('t', Blocks.TORCH)
                .key('e', stairs(Blocks.OAK_STAIRS, Direction.EAST))
                .key('w', stairs(Blocks.OAK_STAIRS, Direction.WEST))
                .layer(
                        "CCCCCCC",
                        "CCCCCCC",
                        "CCCCCCC",
                        "CCCCCCC",
                        "CCCCCCC",
                        "CCCCCCC",
                        "CCCCCCC")
                .layer(
                        "LPPPPPL",
                        "PB..FTP",
                        "Pb...HP",
                        "P....HP",
                        "Pt....P",
                        "P.....P",
                        "LPPDPPL")
                .layer(
                        "LPGGGPL",
                        "P.....P",
                        "G.....G",
                        "G.....G",
                        "G.....G",
                        "P.....P",
                        "LPPdPPL")
                .layer(
                        "LPPPPPL",
                        "P.....P",
                        "P.....P",
                        "P.....P",
                        "P.....P",
                        "P.....P",
                        "LPPPPPL")
                .layer(
                        "ePPPPPw",
                        "e.....w",
                        "e.....w",
                        "e.....w",
                        "e.....w",
                        "e.....w",
                        "ePPPPPw")
                .layer(
                        " ePPPw ",
                        " e...w ",
                        " e...w ",
                        " e...w ",
                        " e...w ",
                        " e...w ",
                        " ePPPw ")
                .layer(
                        "  ePw  ",
                        "  e.w  ",
                        "  e.w  ",
                        "  e.w  ",
                        "  e.w  ",
                        "  e.w  ",
                        "  ePw  ")
                .layer(
                        "   P   ",
                        "   P   ",
                        "   P   ",
                        "   P   ",
                        "   P   ",
                        "   P   ",
                        "   P   ")
                .build();
    }

    private static Blueprint wheatFarm() {
        return Blueprint.builder("wheat_farm", "Wheat Farm", Items.WHEAT)
                .description("A fenced 7x7 wheat field around one water block, which keeps all 48 farmland blocks wet. "
                        + "Torches on the corners stop monsters from spawning inside.")
                .sink(1)
                .key('f', Blocks.FARMLAND)
                .key('W', Blocks.WATER)
                .key('w', Blocks.WHEAT)
                .key('#', Blocks.OAK_FENCE)
                .key('g', Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.NORTH))
                .key('t', Blocks.TORCH)
                .layer(
                        "         ",
                        " fffffff ",
                        " fffffff ",
                        " fffffff ",
                        " fffWfff ",
                        " fffffff ",
                        " fffffff ",
                        " fffffff ",
                        "         ")
                .layer(
                        "#########",
                        "#wwwwwww#",
                        "#wwwwwww#",
                        "#wwwwwww#",
                        "#www.www#",
                        "#wwwwwww#",
                        "#wwwwwww#",
                        "#wwwwwww#",
                        "####g####")
                .layer(
                        "t       t",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "t       t")
                .build();
    }

    private static Blueprint animalPen() {
        return Blueprint.builder("animal_pen", "Animal Pen", Items.OAK_FENCE)
                .description("A fenced pen for cows, sheep and pigs. Lure animals in with wheat (cows, sheep) "
                        + "or carrots (pigs), then breed them for food, leather and wool.")
                .key('#', Blocks.OAK_FENCE)
                .key('g', Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.NORTH))
                .key('t', Blocks.TORCH)
                .layer(
                        "#########",
                        "#       #",
                        "#       #",
                        "#       #",
                        "#       #",
                        "#       #",
                        "#       #",
                        "#       #",
                        "####g####")
                .layer(
                        "t       t",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "         ",
                        "t       t")
                .build();
    }

    private static Blueprint enchantingRoom() {
        return Blueprint.builder("enchanting_room", "Enchanting Room", Items.ENCHANTING_TABLE)
                .description("15 bookshelves around an enchanting table: the exact setup for level 30 enchantments. "
                        + "Keep the space between the table and the shelves empty.")
                .key('R', Blocks.BOOKSHELF)
                .key('E', Blocks.ENCHANTING_TABLE)
                .layer(
                        "RRRRR",
                        "R...R",
                        "R.E.R",
                        "R...R",
                        "RR.RR")
                .layer(
                        "     ",
                        " ... ",
                        " ... ",
                        " ... ",
                        "     ")
                .build();
    }

    private static Blueprint netherPortal() {
        return Blueprint.builder("nether_portal", "Nether Portal", Items.OBSIDIAN)
                .description("The smallest Nether portal: 10 obsidian, corners not needed. "
                        + "Light the inside with flint and steel when the frame is done.")
                .key('O', Blocks.OBSIDIAN)
                .layer(" OO ")
                .layer("O..O")
                .layer("O..O")
                .layer("O..O")
                .layer(" OO ")
                .build();
    }
}
