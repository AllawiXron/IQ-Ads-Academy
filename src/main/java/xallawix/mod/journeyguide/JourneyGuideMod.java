/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.journeyguide;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.slf4j.Logger;
import xallawix.mod.journeyguide.configuration.JourneyGuideClientConfiguration;
import xallawix.mod.journeyguide.configuration.JourneyGuideConfiguration;
import xallawix.mod.journeyguide.init.JourneyGuideModEntities;
import xallawix.mod.journeyguide.network.JourneyActionMessage;
import xallawix.mod.journeyguide.network.JourneySyncMessage;

@Mod("journeyguide")
public class JourneyGuideMod {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "journeyguide";
	private static final int PROTOCOL_VERSION = 1;
	public static SimpleChannel PACKET_HANDLER;

	public JourneyGuideMod(FMLJavaModLoadingContext context) {
		BusGroup bus = context.getModBusGroup();
		JourneyGuideModEntities.REGISTRY.register(bus);

		context.registerConfig(ModConfig.Type.COMMON, JourneyGuideConfiguration.SPEC);
		if (FMLEnvironment.dist == Dist.CLIENT)
			context.registerConfig(ModConfig.Type.CLIENT, JourneyGuideClientConfiguration.SPEC);

		PACKET_HANDLER = ChannelBuilder.named(id("main")).networkProtocolVersion(PROTOCOL_VERSION).simpleChannel()
				.messageBuilder(JourneySyncMessage.class, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(JourneySyncMessage::buffer).decoder(JourneySyncMessage::decode).consumerMainThread(JourneySyncMessage::handler).add()
				.messageBuilder(JourneyActionMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(JourneyActionMessage::buffer).decoder(JourneyActionMessage::decode).consumerMainThread(JourneyActionMessage::handler).add()
				.build();
	}

	public static void sendToPlayer(ServerPlayer player, Object message) {
		PACKET_HANDLER.send(message, PacketDistributor.PLAYER.with(player));
	}

	public static void sendToServer(Object message) {
		PACKET_HANDLER.send(message, PacketDistributor.SERVER.noArg());
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}
