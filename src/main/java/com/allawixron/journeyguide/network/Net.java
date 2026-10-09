package com.allawixron.journeyguide.network;

import com.allawixron.journeyguide.JourneyGuide;
import com.allawixron.journeyguide.journey.JourneyServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class Net {
    private static final int VERSION = 1;
    public static SimpleChannel CHANNEL;

    private Net() {}

    public static void init() {
        CHANNEL = ChannelBuilder.named(JourneyGuide.id("main"))
                .networkProtocolVersion(VERSION)
                .simpleChannel()

                .messageBuilder(SyncPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncPacket::encode)
                .decoder(SyncPacket::decode)
                .consumerMainThread((msg, ctx) -> com.allawixron.journeyguide.client.ClientState.apply(msg))
                .add()

                .messageBuilder(ActionPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ActionPacket::encode)
                .decoder(ActionPacket::decode)
                .consumerMainThread((msg, ctx) -> {
                    ServerPlayer player = ctx.getSender();
                    if (player != null)
                        JourneyServer.handleAction(player, msg);
                })
                .add()

                .build();
    }

    public static void sendTo(ServerPlayer player, Object msg) {
        CHANNEL.send(msg, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToServer(Object msg) {
        CHANNEL.send(msg, PacketDistributor.SERVER.noArg());
    }
}
