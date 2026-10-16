package xallawix.mod.backpack.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import xallawix.mod.backpack.procedures.BackpackButtonProcedure;

public record BackpackActionMessage(int action) {
	public static final int SORT = 0;
	public static final int DEPOSIT = 1;
	public static final int TAKE_ALL = 2;
	public static final int AUTO_PICKUP = 3;

	public static void buffer(BackpackActionMessage message, RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(message.action);
	}

	public static BackpackActionMessage decode(RegistryFriendlyByteBuf buffer) {
		return new BackpackActionMessage(buffer.readVarInt());
	}

	public static void handler(BackpackActionMessage message, CustomPayloadEvent.Context context) {
		BackpackButtonProcedure.execute(context.getSender(), message.action);
	}
}
