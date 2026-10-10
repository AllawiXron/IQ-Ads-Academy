package xallawix.mod.journeyguide.client;

import xallawix.mod.journeyguide.build.BuildCheck;
import xallawix.mod.journeyguide.network.ActionPacket;
import xallawix.mod.journeyguide.network.Net;
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

public final class EasyPlace {
    private EasyPlace() {}

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

    public static @Nullable BlockPos placeablePos(LocalPlayer player) {
        BuildView.CellView target = BuildView.target();
        if (target == null || target.status() != BuildCheck.Status.MISSING)
            return null;
        double distance = BuildView.targetDistance();
        if (distance > player.blockInteractionRange())
            return null;
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit != null && hit.getType() != HitResult.Type.MISS && hit.getLocation().distanceTo(player.getEyePosition()) < distance - 0.05)
            return null;
        return basePos(target.cell().pos(), target.cell().state());
    }

    public static boolean holdsBlockFor(ItemStack stack, BlockState expected) {
        return stack.getItem() instanceof BlockItem item && BuildCheck.sameKind(item.getBlock(), expected.getBlock());
    }

    private static BlockPos basePos(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
            return pos.below();
        if (state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD)
            return pos.relative(state.getValue(BedBlock.FACING).getOpposite());
        return pos;
    }
}
