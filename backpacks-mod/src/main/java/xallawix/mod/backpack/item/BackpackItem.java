package xallawix.mod.backpack.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import xallawix.mod.backpack.BackpackMod;
import xallawix.mod.backpack.procedures.OpenBackpackProcedure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BackpackItem extends Item {
	public static final String AUTO_PICKUP = "AutoPickup";
	public final BackpackTier tier;

	public BackpackItem(BackpackTier tier, Item.Properties properties) {
		super(withTier(tier, properties));
		this.tier = tier;
	}

	private static Item.Properties withTier(BackpackTier tier, Item.Properties properties) {
		properties.stacksTo(1).rarity(tier.rarity).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
				.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER).setSwappable(false).build());
		ItemAttributeModifiers.Builder attributes = ItemAttributeModifiers.builder();
		if (tier.armor > 0)
			attributes.add(Attributes.ARMOR, new AttributeModifier(BackpackMod.id("armor." + tier.name), tier.armor, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
		if (tier.toughness > 0)
			attributes.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(BackpackMod.id("toughness." + tier.name), tier.toughness, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
		properties.attributes(attributes.build());
		if (tier == BackpackTier.NETHERITE)
			properties.fireResistant();
		return properties;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player instanceof ServerPlayer _player) {
			int slot = hand == InteractionHand.OFF_HAND ? Inventory.SLOT_OFFHAND : _player.getInventory().getSelectedSlot();
			OpenBackpackProcedure.open(_player, slot);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	public static boolean autoPickup(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBooleanOr(AUTO_PICKUP, false);
	}

	public static void setAutoPickup(ItemStack stack, boolean enabled) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(AUTO_PICKUP, enabled));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		List<ItemStack> items = contents.nonEmptyItemCopyStream().toList();
		builder.accept(Component.literal(items.size() + " / " + tier.size() + " slots used").withStyle(ChatFormatting.GRAY));
		Map<Item, Integer> counts = new LinkedHashMap<>();
		Map<Item, ItemStack> first = new LinkedHashMap<>();
		for (ItemStack item : items) {
			counts.merge(item.getItem(), item.getCount(), Integer::sum);
			first.putIfAbsent(item.getItem(), item);
		}
		int shown = 0;
		for (Map.Entry<Item, Integer> entry : counts.entrySet()) {
			if (shown == 5) {
				builder.accept(Component.literal("  and " + (counts.size() - 5) + " more...").withStyle(ChatFormatting.DARK_GRAY));
				break;
			}
			builder.accept(Component.literal("  " + entry.getValue() + "x ").withStyle(ChatFormatting.DARK_GRAY).append(first.get(entry.getKey()).getHoverName().copy().withStyle(ChatFormatting.GRAY)));
			shown++;
		}
		if (autoPickup(stack))
			builder.accept(Component.literal("Auto pickup: ON").withStyle(ChatFormatting.GREEN));
		builder.accept(Component.literal("Right-click to open, or wear it and press B").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
	}
}
