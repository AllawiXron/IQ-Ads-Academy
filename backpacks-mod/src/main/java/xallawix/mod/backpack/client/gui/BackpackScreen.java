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
import xallawix.mod.backpack.network.BackpackActionMessage;
import xallawix.mod.backpack.world.inventory.BackpackMenu;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
	private static final ItemStack SORT_ICON = new ItemStack(Items.COMPARATOR);
	private static final ItemStack DEPOSIT_ICON = new ItemStack(Items.HOPPER);
	private static final ItemStack TAKE_ICON = new ItemStack(Items.CHEST);
	private static final ItemStack PICKUP_ON = new ItemStack(Items.DYE.lime());
	private static final ItemStack PICKUP_OFF = new ItemStack(Items.DYE.gray());
	private final Identifier texture;
	private IconButton pickupButton;
	private boolean shownPickup;

	public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, menu.imageWidth, menu.imageHeight);
		this.texture = BackpackMod.id("textures/gui/" + menu.tier.name + "_backpack.png");
		this.inventoryLabelX = (menu.imageWidth - 162) / 2 + 1;
		this.inventoryLabelY = menu.inventoryTop - 11;
	}

	@Override
	protected void init() {
		super.init();
		int x = leftPos + imageWidth + 2;
		int y = topPos + 4;
		addRenderableWidget(new IconButton(x, y, () -> SORT_ICON, Component.literal("Sort").append("\n").append(Component.literal("Stacks and sorts everything inside")),
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.SORT))));
		addRenderableWidget(new IconButton(x, y + 22, () -> DEPOSIT_ICON, Component.literal("Deposit matching").append("\n").append(Component.literal("Moves items you already keep in here from your inventory")),
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.DEPOSIT))));
		addRenderableWidget(new IconButton(x, y + 44, () -> TAKE_ICON, Component.literal("Take all").append("\n").append(Component.literal("Moves everything into your inventory")),
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.TAKE_ALL))));
		pickupButton = addRenderableWidget(new IconButton(x, y + 66, () -> menu.autoPickup() ? PICKUP_ON : PICKUP_OFF, pickupTooltip(),
				b -> BackpackMod.sendToServer(new BackpackActionMessage(BackpackActionMessage.AUTO_PICKUP))));
		shownPickup = menu.autoPickup();
	}

	private Component pickupTooltip() {
		return Component.literal("Auto pickup: " + (menu.autoPickup() ? "ON" : "OFF")).append("\n")
				.append(Component.literal("Items you pick up go straight into this backpack if it already has some of them"));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		if (shownPickup != menu.autoPickup()) {
			shownPickup = menu.autoPickup();
			pickupButton.setTooltip(Tooltip.create(pickupTooltip()));
		}
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, imageWidth, imageHeight);
	}
}
