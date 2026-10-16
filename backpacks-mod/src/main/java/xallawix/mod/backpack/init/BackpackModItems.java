/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.backpack.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xallawix.mod.backpack.BackpackMod;
import xallawix.mod.backpack.item.BackpackItem;
import xallawix.mod.backpack.item.BackpackTier;
import xallawix.mod.backpack.item.EnderBackpackItem;

import java.util.List;

public class BackpackModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, BackpackMod.MODID);
	public static final RegistryObject<Item> LEATHER_BACKPACK = backpack(BackpackTier.LEATHER);
	public static final RegistryObject<Item> IRON_BACKPACK = backpack(BackpackTier.IRON);
	public static final RegistryObject<Item> GOLDEN_BACKPACK = backpack(BackpackTier.GOLDEN);
	public static final RegistryObject<Item> DIAMOND_BACKPACK = backpack(BackpackTier.DIAMOND);
	public static final RegistryObject<Item> EMERALD_BACKPACK = backpack(BackpackTier.EMERALD);
	public static final RegistryObject<Item> NETHERITE_BACKPACK = backpack(BackpackTier.NETHERITE);
	public static final RegistryObject<Item> ENDER_BACKPACK = REGISTRY.register("ender_backpack", () -> new EnderBackpackItem(new Item.Properties().setId(REGISTRY.key("ender_backpack"))));

	public static final List<RegistryObject<Item>> ALL = List.of(LEATHER_BACKPACK, IRON_BACKPACK, GOLDEN_BACKPACK, DIAMOND_BACKPACK, EMERALD_BACKPACK, NETHERITE_BACKPACK, ENDER_BACKPACK);

	private static RegistryObject<Item> backpack(BackpackTier tier) {
		String name = tier.name + "_backpack";
		return REGISTRY.register(name, () -> new BackpackItem(tier, new Item.Properties().setId(REGISTRY.key(name))));
	}
}
