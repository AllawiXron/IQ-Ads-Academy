package xallawix.mod.journeyguide.client;

import xallawix.mod.journeyguide.JourneyGuide;
import xallawix.mod.journeyguide.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Predicate;

public final class ClientInit {
    private ClientInit() {}

    public static void init(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        RegisterKeyMappingsEvent.BUS.addListener(event -> {
            for (KeyMapping key : KeyBindings.ALL)
                event.register(key);
        });
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(event ->
                event.registerEntityRenderer(ModEntities.HOLOGRAM_ANCHOR.get(), HologramRenderer::new));
        AddGuiOverlayLayersEvent.BUS.addListener(event ->
                event.getLayeredDraw().add(ForgeLayeredDraw.PRE_SLEEP_STACK, JourneyGuide.id("tracker"), TrackerHud::extract));
        // returning true cancels the normal right click
        InputEvent.InteractionKeyMappingTriggered.UseItem.BUS.addListener(
                (Predicate<InputEvent.InteractionKeyMappingTriggered.UseItem>) event -> EasyPlace.onUse(event.getHand()));
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> onClientTick());
        TickEvent.RenderTickEvent.Pre.BUS.addListener(event -> GhostRenderer.render());
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(event -> {
            ClientState.reset();
            PlacementMode.cancel();
            Holograms.reset();
        });
    }

    private static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
            return;

        ClientState.tick();

        while (KeyBindings.OPEN_JOURNAL.consumeClick())
            if (mc.gui.screen() == null)
                mc.gui.setScreen(new JournalScreen());
        while (KeyBindings.TOGGLE_TRACKER.consumeClick()) {
            ClientConfig.TrackerMode mode = ClientConfig.cycleMode();
            String name = switch (mode) {
                case COMPACT -> "compact";
                case DETAILED -> "detailed (with tips)";
                case HIDDEN -> "hidden (press H to bring it back)";
            };
            mc.player.sendOverlayMessage(Component.literal("Tracker: " + name).withStyle(ChatFormatting.GOLD));
        }

        if (PlacementMode.active()) {
            PlacementMode.tick();
        } else {
            // don't let presses pile up while not placing
            KeyBindings.ROTATE_BUILD.consumeClick();
            KeyBindings.RAISE_BUILD.consumeClick();
            KeyBindings.LOWER_BUILD.consumeClick();
            KeyBindings.CONFIRM_BUILD.consumeClick();
            KeyBindings.CANCEL_BUILD.consumeClick();
        }
        BuildView.tick();
        Holograms.tick();
    }
}
