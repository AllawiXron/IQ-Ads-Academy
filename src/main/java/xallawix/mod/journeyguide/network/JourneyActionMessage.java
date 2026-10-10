package xallawix.mod.journeyguide.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import xallawix.mod.journeyguide.procedures.JourneyActionProcedure;

public record JourneyActionMessage(Action action, String text, BlockPos pos, Rotation rotation) {
	public enum Action { SKIP_STEP, UNSKIP_STEP, MORE_TIPS, PLACE_BUILD, CANCEL_BUILD, PLACE_BLOCK }

	public static JourneyActionMessage skip(String stepId) {
		return new JourneyActionMessage(Action.SKIP_STEP, stepId, BlockPos.ZERO, Rotation.NONE);
	}

	public static JourneyActionMessage unskip(String stepId) {
		return new JourneyActionMessage(Action.UNSKIP_STEP, stepId, BlockPos.ZERO, Rotation.NONE);
	}

	public static JourneyActionMessage moreTips() {
		return new JourneyActionMessage(Action.MORE_TIPS, "", BlockPos.ZERO, Rotation.NONE);
	}

	public static JourneyActionMessage place(String blueprintId, BlockPos origin, Rotation rotation) {
		return new JourneyActionMessage(Action.PLACE_BUILD, blueprintId, origin, rotation);
	}

	public static JourneyActionMessage placeBlock(BlockPos pos, boolean offhand) {
		return new JourneyActionMessage(Action.PLACE_BLOCK, offhand ? "off" : "main", pos, Rotation.NONE);
	}

	public static JourneyActionMessage cancelBuild() {
		return new JourneyActionMessage(Action.CANCEL_BUILD, "", BlockPos.ZERO, Rotation.NONE);
	}

	public static void buffer(JourneyActionMessage msg, RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(msg.action.ordinal());
		buf.writeUtf(msg.text);
		buf.writeBlockPos(msg.pos);
		buf.writeVarInt(msg.rotation.ordinal());
	}

	public static JourneyActionMessage decode(RegistryFriendlyByteBuf buf) {
		Action[] actions = Action.values();
		Rotation[] rotations = Rotation.values();
		int action = buf.readVarInt();
		String text = buf.readUtf(256);
		BlockPos pos = buf.readBlockPos();
		int rotation = buf.readVarInt();
		return new JourneyActionMessage(actions[Math.floorMod(action, actions.length)], text, pos, rotations[Math.floorMod(rotation, rotations.length)]);
	}

	public static void handler(JourneyActionMessage message, CustomPayloadEvent.Context context) {
		JourneyActionProcedure.execute(context.getSender(), message);
	}
}
