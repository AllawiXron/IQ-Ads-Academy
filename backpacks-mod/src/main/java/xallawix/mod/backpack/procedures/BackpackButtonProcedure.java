package xallawix.mod.backpack.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import xallawix.mod.backpack.network.BackpackActionMessage;
import xallawix.mod.backpack.world.inventory.BackpackMenu;

public class BackpackButtonProcedure {
	public static void execute(Entity entity, int action) {
		if (entity == null)
			return;
		if (!(entity instanceof ServerPlayer _player) || !(_player.containerMenu instanceof BackpackMenu menu))
			return;
		switch (action) {
			case BackpackActionMessage.SORT -> menu.sort();
			case BackpackActionMessage.DEPOSIT -> menu.depositMatching();
			case BackpackActionMessage.TAKE_ALL -> menu.takeAll();
			case BackpackActionMessage.AUTO_PICKUP -> menu.toggleAutoPickup();
			default -> {
				return;
			}
		}
		_player.level().playSound(null, _player.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.6F, 1.0F);
		menu.broadcastChanges();
	}
}
