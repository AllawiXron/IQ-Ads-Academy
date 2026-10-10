package xallawix.mod.journeyguide.build;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

public final class BuildCheck {
    private BuildCheck() {}

    public enum Status { DONE, MISSING, WRONG, OBSTRUCTED }

    public static boolean isClear(BlockState state) {
        return state.isAir() || state.canBeReplaced() || state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.FIRE);
    }

    public static Status status(BlockState world, BlockState expected) {
        if (expected.isAir())
            return isClear(world) ? Status.DONE : Status.OBSTRUCTED;
        if (sameKind(world.getBlock(), expected.getBlock()))
            return Status.DONE;
        return isClear(world) ? Status.MISSING : Status.WRONG;
    }

    public static boolean sameKind(Block world, Block expected) {
        return world == expected || (world instanceof BedBlock && expected instanceof BedBlock);
    }

    public static boolean isComplete(BlockGetter level, List<Blueprint.Cell> cells) {
        for (Blueprint.Cell cell : cells) {
            if (cell.mustBeEmpty())
                continue;
            if (status(level.getBlockState(cell.pos()), cell.state()) != Status.DONE)
                return false;
        }
        return true;
    }

    public static Block materialOf(BlockState state) {
        if (state.hasProperty(DoorBlock.HALF) && state.getBlock() instanceof DoorBlock
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
            return Blocks.AIR;
        if (state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD)
            return Blocks.AIR;
        return state.getBlock();
    }

    public static Item itemFor(Block block) {
        if (block == Blocks.WATER)
            return Items.WATER_BUCKET;
        if (block == Blocks.LAVA)
            return Items.LAVA_BUCKET;
        if (block == Blocks.FARMLAND)
            return Items.WOODEN_HOE;
        Item item = block.asItem();
        return item == Items.AIR ? Items.BARRIER : item;
    }

    public static String placementNote(Block block) {
        if (block == Blocks.FARMLAND)
            return "Till dirt with a hoe";
        if (block == Blocks.WATER)
            return "Pour a water bucket";
        if (block == Blocks.WHEAT)
            return "Plant seeds on farmland";
        if (block == Blocks.TORCH)
            return "Needs a solid block under it";
        return "";
    }

    public static boolean countsInventory(Block block) {
        return block != Blocks.FARMLAND && block != Blocks.WATER;
    }
}
