package xallawix.mod.journeyguide.build;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import org.jspecify.annotations.Nullable;

public final class BlueprintPlacer {
    private BlueprintPlacer() {}

    public static boolean place(ServerPlayer player, Blueprint bp, BlockPos origin, net.minecraft.world.level.block.Rotation rotation,
                                BlockPos pos, InteractionHand hand) {
        Blueprint.Cell cell = findCell(bp, origin, rotation, pos);
        if (cell == null || cell.mustBeEmpty() || BuildCheck.materialOf(cell.state()) == net.minecraft.world.level.block.Blocks.AIR)
            return false;

        ServerLevel level = player.level();
        if (!player.isWithinBlockInteractionRange(pos, 1.0) || !player.mayBuild() || !level.mayInteract(player, pos))
            return false;
        if (!BuildCheck.isClear(level.getBlockState(pos)))
            return false;

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof BlockItem item) || !BuildCheck.sameKind(item.getBlock(), cell.state().getBlock()))
            return false;

        BlockState state = item.getBlock().withPropertiesOf(cell.state());
        if (state.getBlock() instanceof DoorBlock && !level.getBlockState(pos.above()).canBeReplaced()) {
            tell(player, "The door needs an empty block above it.");
            return false;
        }
        if (state.getBlock() instanceof BedBlock && !level.getBlockState(pos.relative(state.getValue(BedBlock.FACING))).canBeReplaced()) {
            tell(player, "The bed needs room for its other half.");
            return false;
        }
        // doors/beds turn into air here without their other half, setPlacedBy adds that half later
        BlockState connected = Block.updateFromNeighbourShapes(state, level, pos);
        if (!connected.isAir())
            state = connected;
        if (!state.canSurvive(level, pos)) {
            tell(player, "That block needs something solid to stand on.");
            return false;
        }

        BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);
        if (!level.setBlock(pos, state, Block.UPDATE_ALL))
            return false;
        if (ForgeEventFactory.onBlockPlace(player, snapshot, Direction.UP)) {
            snapshot.restore(true, false);
            return false;
        }
        state.getBlock().setPlacedBy(level, pos, state, player, stack);

        SoundType sound = state.getSoundType();
        level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, state));
        player.awardStat(Stats.ITEM_USED.get(item));
        CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, stack);
        if (!player.hasInfiniteMaterials())
            stack.shrink(1);
        return true;
    }

    private static Blueprint.@Nullable Cell findCell(Blueprint bp, BlockPos origin, net.minecraft.world.level.block.Rotation rotation, BlockPos pos) {
        for (Blueprint.Cell cell : bp.cells(origin, rotation))
            if (cell.pos().equals(pos))
                return cell;
        return null;
    }

    private static void tell(ServerPlayer player, String message) {
        player.sendOverlayMessage(Component.literal(message).withStyle(ChatFormatting.YELLOW));
    }
}
