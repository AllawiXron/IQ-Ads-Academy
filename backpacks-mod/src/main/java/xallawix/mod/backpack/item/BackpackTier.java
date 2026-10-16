package xallawix.mod.backpack.item;

import net.minecraft.world.item.Rarity;

public enum BackpackTier {
	LEATHER("leather", 9, 3, 1, 0, Rarity.COMMON, 0x8B5A2B),
	IRON("iron", 9, 4, 2, 0, Rarity.COMMON, 0xD8D8D8),
	GOLDEN("golden", 9, 5, 2, 0, Rarity.UNCOMMON, 0xFFD24A),
	DIAMOND("diamond", 9, 6, 3, 1, Rarity.RARE, 0x4AEDD9),
	EMERALD("emerald", 11, 7, 3, 1, Rarity.RARE, 0x17DD62),
	NETHERITE("netherite", 13, 8, 4, 2, Rarity.EPIC, 0x5A575A);

	public final String name;
	public final int columns;
	public final int rows;
	public final int armor;
	public final int toughness;
	public final Rarity rarity;
	public final int color;

	BackpackTier(String name, int columns, int rows, int armor, int toughness, Rarity rarity, int color) {
		this.name = name;
		this.columns = columns;
		this.rows = rows;
		this.armor = armor;
		this.toughness = toughness;
		this.rarity = rarity;
		this.color = color;
	}

	public int size() {
		return columns * rows;
	}

	public static BackpackTier byId(int id) {
		BackpackTier[] tiers = values();
		return tiers[Math.floorMod(id, tiers.length)];
	}
}
