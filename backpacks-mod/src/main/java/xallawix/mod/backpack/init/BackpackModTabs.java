/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package xallawix.mod.backpack.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xallawix.mod.backpack.BackpackMod;

public class BackpackModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BackpackMod.MODID);
	public static final RegistryObject<CreativeModeTab> BACKPACKS = REGISTRY.register("backpacks", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.backpack.backpacks"))
			.icon(() -> new ItemStack(BackpackModItems.DIAMOND_BACKPACK.get()))
			.displayItems((parameters, output) -> {
				for (var item : BackpackModItems.ALL)
					output.accept(item.get());
			}).build());
}
