package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.build.BuildCheck;
import com.allawixron.journeyguide.network.ActionPacket;
import com.allawixron.journeyguide.network.Net;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

/**
 * Right-click a see-through block while holding the right block, and it's placed right there, facing the
 * right way. You don't need a block next to it to click on.
 */
public final class EasyPlace {
    private EasyPlace() {}

    /** Called on right click. @return true if we placed into the hologram (the normal click is cancelled). */
    public static boolean onUse(InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || PlacementMode.active())
            return false;
        BlockPos pos = placeablePos(player);
        if (pos == null || !holdsBlockFor(player.getItemInHand(hand), BuildView.target().cell().state()))
            return false;
        Net.sendToServer(ActionPacket.placeBlock(pos, hand == InteractionHand.OFF_HAND));
        return true;
    }

    /** Where right-clicking would place a block, or null if you're not aiming at an empty hologram spot in reach. */
    public static @Nullable BlockPos placeablePos(LocalPlayer player) {
        BuildView.CellView target = BuildView.target();
        if (target == null || target.status() != BuildCheck.Status.MISSING)
            return null;
        double distance = BuildView.targetDistance();
        if (distance > player.blockInteractionRange())
            return null;
        // a real block (or mob) in front of the hologram block gets the click instead
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit != null && hit.getType() != HitResult.Type.MISS && hit.getLocation().distanceTo(player.getEyePosition()) < distance - 0.05)
            return null;
        return basePos(target.cell().pos(), target.cell().state());
    }

    public static boolean holdsBlockFor(ItemStack stack, BlockState expected) {
        return stack.getItem() instanceof BlockItem item && BuildCheck.sameKind(item.getBlock(), expected.getBlock());
    }

    /** The top of a door and the head of a bed come with the other half; point at the half you actually place. */
    private static BlockPos basePos(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
            return pos.below();
        if (state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD)
            return pos.relative(state.getValue(BedBlock.FACING).getOpposite());
        return pos;
    }
}
