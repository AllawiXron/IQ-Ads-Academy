package xallawix.mod.backpack.procedures;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import xallawix.mod.backpack.item.BackpackItem;
import xallawix.mod.backpack.item.EnderBackpackItem;
import xallawix.mod.backpack.world.inventory.BackpackMenu;

public class OpenBackpackProcedure {
	private static final int CHEST_SLOT = 38;

	// B key: the backpack you wear first, then your offhand, then the hotbar and the rest of your inventory
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof ServerPlayer _player))
			return;
		Inventory inventory = _player.getInventory();
		int[] order = new int[2 + Inventory.INVENTORY_SIZE];
		order[0] = CHEST_SLOT;
		order[1] = Inventory.SLOT_OFFHAND;
		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
			order[2 + i] = i;
		for (int slot : order) {
			ItemStack stack = inventory.getItem(slot);
			if (stack.getItem() instanceof BackpackItem) {
				open(_player, slot);
				return;
			}
			if (stack.getItem() instanceof EnderBackpackItem) {
				openEnder(_player);
				return;
			}
		}
		_player.sendOverlayMessage(Component.literal("You don't have a backpack with you"));
	}

	public static void open(ServerPlayer player, int slot) {
		ItemStack stack = player.getInventory().getItem(slot);
		if (!(stack.getItem() instanceof BackpackItem item))
			return;
		player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new BackpackMenu(id, inventory, item.tier, slot, stack), stack.getHoverName()), buffer -> {
			buffer.writeVarInt(item.tier.ordinal());
			buffer.writeVarInt(slot);
		});
		player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 0.8F, 1.1F);
	}

	public static void openEnder(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider((id, inventory, p) -> ChestMenu.threeRows(id, inventory, p.getEnderChestInventory()), Component.translatable("item.backpack.ender_backpack")));
		player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 0.6F, 1.1F);
	}
}
