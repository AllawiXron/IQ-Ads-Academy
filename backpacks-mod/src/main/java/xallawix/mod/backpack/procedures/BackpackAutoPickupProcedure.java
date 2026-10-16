package xallawix.mod.backpack.procedures;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xallawix.mod.backpack.item.BackpackItem;
import xallawix.mod.backpack.world.inventory.BackpackMenu;

@Mod.EventBusSubscriber
public class BackpackAutoPickupProcedure {
	// returning true cancels the normal pickup (only when the backpacks took everything)
	@SubscribeEvent
	public static boolean onItemPickup(EntityItemPickupEvent event) {
		return execute(event.getEntity(), event.getItem());
	}

	public static boolean execute(Player entity, ItemEntity itemEntity) {
		if (entity == null || itemEntity == null)
			return false;
		if (!(entity instanceof ServerPlayer _player) || itemEntity.hasPickUpDelay())
			return false;
		ItemStack picked = itemEntity.getItem();
		if (picked.isEmpty() || picked.getItem() instanceof BackpackItem)
			return false;
		int before = picked.getCount();
		Inventory inventory = _player.getInventory();
		for (int slot = 0; slot <= Inventory.SLOT_OFFHAND && !picked.isEmpty(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!(stack.getItem() instanceof BackpackItem item) || !BackpackItem.autoPickup(stack))
				continue;
			if (_player.containerMenu instanceof BackpackMenu menu && menu.backpack == stack) {
				if (BackpackMenu.contains(menu.container.getItems(), picked)) {
					BackpackMenu.insert(menu.container.getItems(), picked);
					menu.container.setChanged();
				}
				continue;
			}
			NonNullList<ItemStack> items = NonNullList.withSize(item.tier.size(), ItemStack.EMPTY);
			stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
			if (!BackpackMenu.contains(items, picked))
				continue;
			BackpackMenu.insert(items, picked);
			stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
		}
		int taken = before - picked.getCount();
		if (taken <= 0)
			return false;
		if (picked.isEmpty()) {
			_player.take(itemEntity, taken);
			itemEntity.discard();
			_player.level().playSound(null, _player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.4F);
			return true;
		}
		itemEntity.setItem(picked);
		return false;
	}
}
