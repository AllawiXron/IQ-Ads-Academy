package xallawix.mod.journeyguide.client;

import xallawix.mod.journeyguide.init.JourneyGuideModKeyMappings;

import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.network.JourneyActionMessage;
import xallawix.mod.journeyguide.JourneyGuideMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class PlacementMode {
	private static @Nullable Blueprint blueprint;
	private static Rotation extraRotation = Rotation.NONE;
	private static int yOffset;
	private static BlockPos origin = BlockPos.ZERO;
	private static Rotation rotation = Rotation.NONE;
	private static List<Blueprint.Cell> cells = List.of();


	public static void start(Blueprint bp) {
		blueprint = bp;
		extraRotation = Rotation.NONE;
		yOffset = 0;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null)
			mc.player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
					.append(Component.literal("Look where you want the " + bp.name + ". ")
							.withStyle(ChatFormatting.YELLOW))
					.append(Component.literal("R").withStyle(ChatFormatting.AQUA))
					.append(Component.literal(" rotate, ").withStyle(ChatFormatting.YELLOW))
					.append(Component.literal("Up/Down").withStyle(ChatFormatting.AQUA))
					.append(Component.literal(" move up/down, ").withStyle(ChatFormatting.YELLOW))
					.append(Component.literal("Enter").withStyle(ChatFormatting.AQUA))
					.append(Component.literal(" place, ").withStyle(ChatFormatting.YELLOW))
					.append(Component.literal("Backspace").withStyle(ChatFormatting.AQUA))
					.append(Component.literal(" cancel.").withStyle(ChatFormatting.YELLOW)));
	}

	public static boolean active() {
		return blueprint != null;
	}

	public static @Nullable Blueprint blueprint() {
		return blueprint;
	}

	public static List<Blueprint.Cell> cells() {
		return cells;
	}

	public static void cancel() {
		blueprint = null;
		cells = List.of();
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (blueprint == null || player == null || mc.level == null)
			return;

		while (JourneyGuideModKeyMappings.ROTATE_BUILD.consumeClick()) {
			extraRotation = extraRotation.getRotated(Rotation.CLOCKWISE_90);
			player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, 1.6F);
		}
		while (JourneyGuideModKeyMappings.RAISE_BUILD.consumeClick())
			yOffset++;
		while (JourneyGuideModKeyMappings.LOWER_BUILD.consumeClick())
			yOffset--;

		BlockPos anchor = inFront(player, anchor(player)).above(yOffset);
		rotation = Blueprint.rotationFor(player.getDirection()).getRotated(extraRotation);
		origin = blueprint.originFor(anchor, rotation);
		cells = blueprint.cells(origin, rotation);

		while (JourneyGuideModKeyMappings.CONFIRM_BUILD.consumeClick()) {
			JourneyGuideMod.sendToServer(JourneyActionMessage.place(blueprint.id, origin, rotation));
			player.playSound(SoundEvents.PLAYER_LEVELUP, 0.4F, 1.8F);
			player.sendSystemMessage(Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
					.append(Component.literal("Placed! Hold the right block and right-click the see-through blocks "
							+ "to place them. The tracker shows what you still need.").withStyle(ChatFormatting.YELLOW)));
			cancel();
			return;
		}
		while (JourneyGuideModKeyMappings.CANCEL_BUILD.consumeClick()) {
			cancel();
			return;
		}
	}

	private static BlockPos inFront(LocalPlayer player, BlockPos anchor) {
		Direction facing = player.getDirection();
		BlockPos feet = player.blockPosition();
		int ahead = (anchor.getX() - feet.getX()) * facing.getStepX() + (anchor.getZ() - feet.getZ()) * facing.getStepZ();
		return ahead < 2 ? anchor.relative(facing, 2 - ahead) : anchor;
	}

	private static BlockPos anchor(LocalPlayer player) {
		HitResult hit = player.pick(32.0, 1.0F, false);
		if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
			Direction face = blockHit.getDirection();
			BlockPos pos = blockHit.getBlockPos();
			return face == Direction.UP ? pos.above() : pos.relative(face);
		}
		Vec3 ahead = player.position().add(player.getViewVector(1.0F).multiply(1, 0, 1).normalize().scale(8));
		return BlockPos.containing(ahead);
	}
}
