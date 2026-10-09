package com.allawixron.journeyguide;

import com.allawixron.journeyguide.journey.JourneyServer;
import com.allawixron.journeyguide.network.Net;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(JourneyGuide.MODID)
public final class JourneyGuide {
    public static final String MODID = "journeyguide";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JourneyGuide(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        Net.init();
        JourneyServer.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            com.allawixron.journeyguide.client.ClientInit.init(context);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
