package xallawix.mod.backpack.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class IconButton extends Button {
	private final Supplier<ItemStack> icon;

	public IconButton(int x, int y, Supplier<ItemStack> icon, Component tooltip, Button.OnPress onPress) {
		super(x, y, 20, 20, tooltip, onPress, DEFAULT_NARRATION);
		this.icon = icon;
		setTooltip(Tooltip.create(tooltip));
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		extractDefaultSprite(graphics);
		graphics.item(icon.get(), getX() + 2, getY() + 2);
	}
}
