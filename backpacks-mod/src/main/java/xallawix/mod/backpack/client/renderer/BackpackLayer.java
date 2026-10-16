package xallawix.mod.backpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import xallawix.mod.backpack.item.BackpackItem;
import xallawix.mod.backpack.item.EnderBackpackItem;

// draws the backpack you wear in the chest slot on your back
public class BackpackLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
	private final ItemStackRenderState itemState = new ItemStackRenderState();

	public BackpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
		super(renderer);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || state.isInvisible)
			return;
		Entity entity = mc.level.getEntity(state.id);
		if (!(entity instanceof LivingEntity living))
			return;
		ItemStack stack = living.getItemBySlot(EquipmentSlot.CHEST);
		if (!(stack.getItem() instanceof BackpackItem) && !(stack.getItem() instanceof EnderBackpackItem))
			return;
		mc.getItemModelResolver().updateForLiving(itemState, stack, ItemDisplayContext.HEAD, living);
		poseStack.pushPose();
		getParentModel().body.translateAndRotate(poseStack);
		// straps against the back of the body, pocket facing out
		poseStack.translate(0.0F, 0.38F, 0.30F);
		poseStack.scale(0.7F, -0.7F, -0.7F);
		itemState.submit(poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}
}
