package xallawix.mod.backpack.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import xallawix.mod.backpack.procedures.OpenBackpackProcedure;

import java.util.function.Consumer;

public class EnderBackpackItem extends Item {
	public EnderBackpackItem(Item.Properties properties) {
		super(properties.stacksTo(1).rarity(Rarity.RARE)
				.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER).setSwappable(false).build()));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player instanceof ServerPlayer _player)
			OpenBackpackProcedure.openEnder(_player);
		return InteractionResult.SUCCESS;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		builder.accept(Component.literal("Opens your ender chest from anywhere").withStyle(ChatFormatting.LIGHT_PURPLE));
		builder.accept(Component.literal("Right-click to open, or wear it and press B").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
	}
}
