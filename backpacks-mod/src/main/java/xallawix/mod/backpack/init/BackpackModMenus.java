/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.backpack.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xallawix.mod.backpack.BackpackMod;
import xallawix.mod.backpack.world.inventory.BackpackMenu;

public class BackpackModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, BackpackMod.MODID);
	public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK = REGISTRY.register("backpack", () -> IForgeMenuType.create(BackpackMenu::new));
}
