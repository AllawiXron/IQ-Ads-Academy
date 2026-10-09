package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.build.BuildCheck;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Locale;

/** Plain-language advice on how to place a block so it ends up facing the right way. */
public final class BlockHints {
    private BlockHints() {}

    public static String placement(BlockState state) {
        Block block = state.getBlock();
        String note = BuildCheck.placementNote(block);
        if (!note.isEmpty())
            return note;
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
            return "";
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        String dir = facing.getName().toLowerCase(Locale.ROOT);
        if (block instanceof StairBlock || block instanceof DoorBlock || block instanceof BedBlock
                || block instanceof FenceGateBlock)
            return "Look " + dir + " while placing";
        if (block instanceof TrapDoorBlock)
            return "Look " + facing.getOpposite().getName().toLowerCase(Locale.ROOT) + " while placing";
        return "Stand on its " + dir + " side, facing it";
    }
}
