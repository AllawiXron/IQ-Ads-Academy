package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.JourneyGuide;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ClientInit {
    private ClientInit() {}

    public static void init(FMLJavaModLoadingContext context) {
        RegisterKeyMappingsEvent.BUS.addListener(event -> {
            for (KeyMapping key : KeyBindings.ALL)
                event.register(key);
        });
        AddGuiOverlayLayersEvent.BUS.addListener(event ->
                event.getLayeredDraw().add(ForgeLayeredDraw.PRE_SLEEP_STACK, JourneyGuide.id("tracker"), TrackerHud::extract));
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> onClientTick());
        TickEvent.RenderTickEvent.Pre.BUS.addListener(event -> GhostRenderer.render());
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(event -> {
            ClientState.reset();
            PlacementMode.cancel();
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
        while (KeyBindings.TOGGLE_TRACKER.consumeClick())
            TrackerHud.visible = !TrackerHud.visible;

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
    }
}
