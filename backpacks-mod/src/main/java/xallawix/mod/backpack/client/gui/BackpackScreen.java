package xallawix.mod.backpack.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xallawix.mod.backpack.BackpackMod;
import xallawix.mod.backpack.init.BackpackModItems;
import xallawix.mod.backpack.network.BackpackActionMessage;
import xallawix.mod.backpack.world.inventory.BackpackMenu;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
	private static final ItemStack SORT_ICON = new ItemStack(Items.COMPARATOR);
	private static final ItemStack DEPOSIT_ICON = new ItemStack(Items.HOPPER);
	private static final ItemStack TAKE_ICON = new ItemStack(Items.CHEST);
	private static final ItemStack PICKUP_ICON = new ItemStack(Items.BUNDLE);
	private static final int TAB_WIDTH = 28;
	private final Identifier texture;
	private final ItemStack headerIcon;
	private IconButton pickupButton;
	private boolean shownPickup;

	public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, menu.imageWidth, menu.imageHeight);
		this.texture = BackpackMod.id("textures/gui/" + menu.tier.name + "_backpack.png");
		this.headerIcon = new ItemStack(BackpackModItems.ALL.get(menu.tier.ordinal()).get());
		this.titleLabelX = 23;
		this.titleLabelY = 7;
		this.inventoryLabelX = (menu.imageWidth - 162) / 2 + 1;
		this.inventoryLabelY = menu.inventoryTop - 11;
	}

	@Override
	protected void init() {
		super.init();
		int x = leftPos + imageWidth + 2;
		int y = topPos + 26;
		addRenderableWidget(new IconButton(x, y, () -> SORT_ICON, tip("Sort", "Stacks and sorts everything inside"), null,
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.SORT))));
		addRenderableWidget(new IconButton(x, y + 22, () -> DEPOSIT_ICON, tip("Deposit matching", "Moves items you already keep in here from your inventory"), null,
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.DEPOSIT))));
		addRenderableWidget(new IconButton(x, y + 44, () -> TAKE_ICON, tip("Take all", "Moves everything into your inventory"), null,
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.TAKE_ALL))));
		pickupButton = addRenderableWidget(new IconButton(x, y + 66, () -> PICKUP_ICON, pickupTooltip(), () -> menu.autoPickup() ? 0xFF55FF55 : 0xFF8B8B8B,
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.AUTO_PICKUP))));
		shownPickup = menu.autoPickup();
	}

	private static Component tip(String title, String text) {
		return Component.literal(title).append("\n").append(Component.literal(text).withStyle(net.minecraft.ChatFormatting.GRAY));
	}

	private Component pickupTooltip() {
		return tip("Auto pickup: " + (menu.autoPickup() ? "ON" : "OFF"), "Items you pick up go straight into this backpack if it already has some of them");
	}

	private int usedSlots() {
		int used = 0;
		for (int i = 0; i < menu.tier.size(); i++) {
			if (menu.slots.get(i).hasItem())
				used++;
		}
		return used;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		if (shownPickup != menu.autoPickup()) {
			shownPickup = menu.autoPickup();
			pickupButton.setTooltip(Tooltip.create(pickupTooltip()));
		}
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth + TAB_WIDTH, imageHeight, imageWidth + TAB_WIDTH, imageHeight);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.item(headerIcon, 4, 2);
		graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, true);
		String count = usedSlots() + "/" + menu.tier.size();
		graphics.text(font, count, imageWidth - 7 - font.width(count), titleLabelY, 0xFFE0E0E0, true);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFF404040, false);
	}
}
