package xallawix.mod.journeyguide.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import xallawix.mod.journeyguide.journey.PlayerProgress;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JourneyGuideModVariables {
	public static final String DATA_KEY = "journeyguide";
	private static final Map<UUID, PlayerProgress> PLAYER_VARIABLES = new HashMap<>();

	public static PlayerProgress get(ServerPlayer player) {
		return PLAYER_VARIABLES.computeIfAbsent(player.getUUID(), uuid -> {
			CompoundTag persisted = player.getPersistentData().getCompoundOrEmpty(ServerPlayer.PERSISTED_NBT_TAG);
			return PlayerProgress.load(persisted.getCompoundOrEmpty(DATA_KEY));
		});
	}

	public static void save(ServerPlayer player, PlayerProgress progress) {
		CompoundTag root = player.getPersistentData();
		CompoundTag persisted = root.getCompoundOrEmpty(ServerPlayer.PERSISTED_NBT_TAG);
		persisted.put(DATA_KEY, progress.save());
		root.put(ServerPlayer.PERSISTED_NBT_TAG, persisted);
	}

	public static void set(ServerPlayer player, PlayerProgress progress) {
		PLAYER_VARIABLES.put(player.getUUID(), progress);
		save(player, progress);
	}

	public static void forget(ServerPlayer player) {
		PLAYER_VARIABLES.remove(player.getUUID());
	}
}
