package xallawix.mod.backpack.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import xallawix.mod.backpack.init.BackpackModMenus;
import xallawix.mod.backpack.item.BackpackItem;
import xallawix.mod.backpack.item.BackpackTier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BackpackMenu extends AbstractContainerMenu {
	public static final int SLOTS_TOP = 24;
	public final BackpackTier tier;
	public final int backpackSlot;
	public final ItemStack backpack;
	public final BackpackContainer container;
	private final Player player;
	private final DataSlot autoPickup = DataSlot.standalone();
	public final int imageWidth;
	public final int imageHeight;
	public final int inventoryTop;

	// client side
	public BackpackMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
		this(id, inventory, BackpackTier.byId(buffer.readVarInt()), buffer.readVarInt(), ItemStack.EMPTY);
	}

	// server side
	public BackpackMenu(int id, Inventory inventory, BackpackTier tier, int backpackSlot, ItemStack backpack) {
		super(BackpackModMenus.BACKPACK.get(), id);
		this.tier = tier;
		this.backpackSlot = backpackSlot;
		this.backpack = backpack;
		this.player = inventory.player;
		this.container = new BackpackContainer(tier.size(), backpack);
		if (!backpack.isEmpty()) {
			backpack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(container.getItems());
			autoPickup.set(BackpackItem.autoPickup(backpack) ? 1 : 0);
		}
		addDataSlot(autoPickup);

		imageWidth = Math.max(176, tier.columns * 18 + 14);
		inventoryTop = SLOTS_TOP + tier.rows * 18 + 16;
		imageHeight = inventoryTop + 83;
		int left = (imageWidth - tier.columns * 18) / 2 + 1;
		for (int row = 0; row < tier.rows; row++) {
			for (int col = 0; col < tier.columns; col++)
				addSlot(new BackpackSlot(container, col + row * tier.columns, left + col * 18, SLOTS_TOP + row * 18));
		}
		int invLeft = (imageWidth - 162) / 2 + 1;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++)
				addPlayerSlot(inventory, col + row * 9 + 9, invLeft + col * 18, inventoryTop + row * 18);
		}
		for (int col = 0; col < 9; col++)
			addPlayerSlot(inventory, col, invLeft + col * 18, inventoryTop + 58);
	}

	private void addPlayerSlot(Inventory inventory, int index, int x, int y) {
		addSlot(index == backpackSlot ? new LockedSlot(inventory, index, x, y) : new Slot(inventory, index, x, y));
	}

	public boolean autoPickup() {
		return autoPickup.get() == 1;
	}

	@Override
	public boolean stillValid(Player player) {
		if (backpack.isEmpty())
			return true;
		return player.isAlive() && player.getInventory().getItem(backpackSlot) == backpack;
	}

	@Override
	public void clicked(int slotIndex, int buttonNum, ContainerInput input, Player player) {
		// the opened backpack can't be moved out from under you (also blocks the number-key and offhand swap)
		if (input == ContainerInput.SWAP && (buttonNum == backpackSlot || (buttonNum == 40 && backpackSlot == Inventory.SLOT_OFFHAND)))
			return;
		if (slotIndex >= 0 && slotIndex < slots.size() && slots.get(slotIndex) instanceof LockedSlot)
			return;
		super.clicked(slotIndex, buttonNum, input, player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack clicked = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			clicked = stack.copy();
			int size = container.getContainerSize();
			if (slotIndex < size) {
				if (!moveItemStackTo(stack, size, slots.size(), true))
					return ItemStack.EMPTY;
			} else if (!moveItemStackTo(stack, 0, size, false)) {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty())
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();
		}
		return clicked;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		container.setChanged();
	}

	public void sort() {
		List<ItemStack> merged = new ArrayList<>();
		for (ItemStack stack : container.getItems()) {
			if (stack.isEmpty())
				continue;
			ItemStack rest = stack.copy();
			for (ItemStack target : merged) {
				if (rest.isEmpty())
					break;
				if (ItemStack.isSameItemSameComponents(target, rest) && target.getCount() < target.getMaxStackSize()) {
					int move = Math.min(rest.getCount(), target.getMaxStackSize() - target.getCount());
					target.grow(move);
					rest.shrink(move);
				}
			}
			if (!rest.isEmpty())
				merged.add(rest);
		}
		merged.sort(Comparator.comparing((ItemStack s) -> s.getItem().getDescriptionId()).thenComparing(s -> -s.getCount()));
		for (int i = 0; i < container.getContainerSize(); i++)
			container.getItems().set(i, i < merged.size() ? merged.get(i) : ItemStack.EMPTY);
		container.setChanged();
	}

	public void depositMatching() {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
			if (i == backpackSlot)
				continue;
			ItemStack stack = inventory.getItem(i);
			if (stack.isEmpty() || stack.getItem() instanceof BackpackItem || !contains(container.getItems(), stack))
				continue;
			insert(container.getItems(), stack);
		}
		container.setChanged();
	}

	public void takeAll() {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if (!stack.isEmpty())
				player.getInventory().add(stack);
		}
		container.setChanged();
	}

	public void toggleAutoPickup() {
		boolean enabled = !BackpackItem.autoPickup(backpack);
		BackpackItem.setAutoPickup(backpack, enabled);
		autoPickup.set(enabled ? 1 : 0);
	}

	public static boolean contains(NonNullList<ItemStack> items, ItemStack stack) {
		for (ItemStack item : items) {
			if (ItemStack.isSameItemSameComponents(item, stack))
				return true;
		}
		return false;
	}

	// moves as much of the stack as fits into the items, filling matching stacks first
	public static void insert(NonNullList<ItemStack> items, ItemStack stack) {
		for (ItemStack item : items) {
			if (stack.isEmpty())
				return;
			if (ItemStack.isSameItemSameComponents(item, stack) && item.getCount() < item.getMaxStackSize()) {
				int move = Math.min(stack.getCount(), item.getMaxStackSize() - item.getCount());
				item.grow(move);
				stack.shrink(move);
			}
		}
		for (int i = 0; i < items.size() && !stack.isEmpty(); i++) {
			if (items.get(i).isEmpty())
				items.set(i, stack.split(stack.getMaxStackSize()));
		}
	}

	public static class BackpackContainer extends SimpleContainer {
		private final ItemStack backpack;

		public BackpackContainer(int size, ItemStack backpack) {
			super(size);
			this.backpack = backpack;
		}

		@Override
		public void setChanged() {
			super.setChanged();
			if (!backpack.isEmpty())
				backpack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(getItems()));
		}
	}

	public static class BackpackSlot extends Slot {
		public BackpackSlot(BackpackContainer container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return stack.getItem().canFitInsideContainerItems();
		}
	}

	public static class LockedSlot extends Slot {
		public LockedSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean mayPickup(Player player) {
			return false;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	}
}
