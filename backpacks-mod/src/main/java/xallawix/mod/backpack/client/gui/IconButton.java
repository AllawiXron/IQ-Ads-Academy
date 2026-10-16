package xallawix.mod.backpack.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

// a slot-style button on the backpack's side tab: just the icon, a soft highlight on hover and an optional status light
public class IconButton extends Button {
	private final Supplier<ItemStack> icon;
	private final IntSupplier light;

	public IconButton(int x, int y, Supplier<ItemStack> icon, Component tooltip, IntSupplier light, Button.OnPress onPress) {
		super(x, y, 20, 20, tooltip, onPress, DEFAULT_NARRATION);
		this.icon = icon;
		this.light = light;
		setTooltip(Tooltip.create(tooltip));
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (isHoveredOrFocused())
			graphics.fill(getX(), getY(), getX() + 20, getY() + 20, 0x80FFFFFF);
		graphics.item(icon.get(), getX() + 2, getY() + 2);
		int color = light == null ? 0 : light.getAsInt();
		if (color != 0) {
			graphics.fill(getX() + 14, getY() + 1, getX() + 19, getY() + 6, 0xFF1A1A1A);
			graphics.fill(getX() + 15, getY() + 2, getX() + 18, getY() + 5, color);
		}
	}
}
