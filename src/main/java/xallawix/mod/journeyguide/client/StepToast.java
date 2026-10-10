package xallawix.mod.journeyguide.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class StepToast implements Toast {
    private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("toast/advancement");
    private static final long DISPLAY_MS = 5000;

    private final String header;
    private final String text;
    private final ItemStack icon;
    private Visibility visibility = Visibility.SHOW;

    private StepToast(String header, String text, ItemStack icon) {
        this.header = header;
        this.text = text;
        this.icon = icon;
    }

    public static void show(String header, String text, ItemStack icon) {
        Minecraft.getInstance().gui.toastManager().addToast(new StepToast(header, text, icon));
    }

    @Override
    public Visibility getWantedVisibility() {
        return visibility;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs) {
        visibility = fullyVisibleForMs >= DISPLAY_MS * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 0, 0, width(), height());
        graphics.text(font, header, 30, 7, 0xFFFFD34D, false);
        List<FormattedCharSequence> lines = font.split(Component.literal(text), 125);
        if (!lines.isEmpty())
            graphics.text(font, lines.getFirst(), 30, 18, 0xFFFFFFFF, false);
        graphics.fakeItem(icon, 8, 8);
    }
}
