package xallawix.mod.backpack.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import xallawix.mod.backpack.procedures.OpenBackpackProcedure;

public record OpenBackpackMessage() {
	public static void buffer(OpenBackpackMessage message, RegistryFriendlyByteBuf buffer) {
	}

	public static OpenBackpackMessage decode(RegistryFriendlyByteBuf buffer) {
		return new OpenBackpackMessage();
	}

	public static void handler(OpenBackpackMessage message, CustomPayloadEvent.Context context) {
		OpenBackpackProcedure.execute(context.getSender());
	}
}
