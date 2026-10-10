package xallawix.mod.journeyguide.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.Rotation;

public record ActionPacket(Action action, String text, BlockPos pos, Rotation rotation) {
    public enum Action { SKIP_STEP, UNSKIP_STEP, MORE_TIPS, PLACE_BUILD, CANCEL_BUILD, PLACE_BLOCK }

    public static ActionPacket skip(String stepId) {
        return new ActionPacket(Action.SKIP_STEP, stepId, BlockPos.ZERO, Rotation.NONE);
    }

    public static ActionPacket unskip(String stepId) {
        return new ActionPacket(Action.UNSKIP_STEP, stepId, BlockPos.ZERO, Rotation.NONE);
    }

    public static ActionPacket moreTips() {
        return new ActionPacket(Action.MORE_TIPS, "", BlockPos.ZERO, Rotation.NONE);
    }

    public static ActionPacket place(String blueprintId, BlockPos origin, Rotation rotation) {
        return new ActionPacket(Action.PLACE_BUILD, blueprintId, origin, rotation);
    }

    public static ActionPacket placeBlock(BlockPos pos, boolean offhand) {
        return new ActionPacket(Action.PLACE_BLOCK, offhand ? "off" : "main", pos, Rotation.NONE);
    }

    public static ActionPacket cancelBuild() {
        return new ActionPacket(Action.CANCEL_BUILD, "", BlockPos.ZERO, Rotation.NONE);
    }

    public static void encode(ActionPacket msg, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(msg.action.ordinal());
        buf.writeUtf(msg.text);
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.rotation.ordinal());
    }

    public static ActionPacket decode(RegistryFriendlyByteBuf buf) {
        Action[] actions = Action.values();
        Rotation[] rotations = Rotation.values();
        int action = buf.readVarInt();
        String text = buf.readUtf(256);
        BlockPos pos = buf.readBlockPos();
        int rotation = buf.readVarInt();
        return new ActionPacket(actions[Math.floorMod(action, actions.length)], text, pos, rotations[Math.floorMod(rotation, rotations.length)]);
    }
}
