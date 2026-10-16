/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.backpack;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.slf4j.Logger;
import xallawix.mod.backpack.client.gui.BackpackScreen;
import xallawix.mod.backpack.init.BackpackModItems;
import xallawix.mod.backpack.init.BackpackModMenus;
import xallawix.mod.backpack.init.BackpackModTabs;
import xallawix.mod.backpack.network.BackpackActionMessage;
import xallawix.mod.backpack.network.OpenBackpackMessage;

@Mod("backpack")
public class BackpackMod {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "backpack";
	private static final int PROTOCOL_VERSION = 1;
	public static SimpleChannel PACKET_HANDLER;

	public BackpackMod(FMLJavaModLoadingContext context) {
		BusGroup bus = context.getModBusGroup();
		BackpackModItems.REGISTRY.register(bus);
		BackpackModMenus.REGISTRY.register(bus);
		BackpackModTabs.REGISTRY.register(bus);

		PACKET_HANDLER = ChannelBuilder.named(id("main")).networkProtocolVersion(PROTOCOL_VERSION).simpleChannel()
				.messageBuilder(OpenBackpackMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(OpenBackpackMessage::buffer).decoder(OpenBackpackMessage::decode).consumerMainThread(OpenBackpackMessage::handler).add()
				.messageBuilder(BackpackActionMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(BackpackActionMessage::buffer).decoder(BackpackActionMessage::decode).consumerMainThread(BackpackActionMessage::handler).add()
				.build();

		if (FMLEnvironment.dist == Dist.CLIENT)
			FMLClientSetupEvent.getBus(bus).addListener(event -> event.enqueueWork(() -> MenuScreens.register(BackpackModMenus.BACKPACK.get(), BackpackScreen::new)));
	}

	public static void sendToServer(Object message) {
		PACKET_HANDLER.send(message, PacketDistributor.SERVER.noArg());
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}
