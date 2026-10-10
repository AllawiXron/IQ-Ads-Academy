package xallawix.mod.journeyguide.client.renderer;

import xallawix.mod.journeyguide.client.Holograms;

import xallawix.mod.journeyguide.entity.HologramAnchorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class HologramAnchorRenderer extends EntityRenderer<HologramAnchorEntity, HologramAnchorRenderer.State> {
	private static final RenderType GHOST = RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS, false);
	private static final float INSET = 0.985F;

	public static final class State extends EntityRenderState {
		List<Holograms.Ghost> ghosts = List.of();
	}

	public HologramAnchorRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(HologramAnchorEntity entity, State state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.ghosts = Holograms.ghosts();
	}

	@Override
	protected boolean affectedByCulling(HologramAnchorEntity entity) {
		return false;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.ghosts.isEmpty())
			return;
		float pulse = 0.85F + 0.15F * (float) Math.sin(Util.getMillis() / 300.0);
		var models = Minecraft.getInstance().getModelManager().getBlockStateModelSet();

		for (Holograms.Ghost ghost : state.ghosts) {
			BlockStateModel model = models.get(ghost.state());
			List<BlockStateModelPart> parts = new ArrayList<>();
			model.collectParts(RandomSource.create(ghost.pos().asLong()), parts);
			if (parts.isEmpty())
				continue;
			float alpha = ghost.pulse() ? ghost.alpha() * pulse : ghost.alpha();
			int a = Math.round(alpha * 255);

			poseStack.pushPose();
			poseStack.translate(ghost.pos().getX() - state.x, ghost.pos().getY() - state.y, ghost.pos().getZ() - state.z);
			poseStack.translate(0.5F, 0.5F, 0.5F);
			poseStack.scale(INSET, INSET, INSET);
			poseStack.translate(-0.5F, -0.5F, -0.5F);
			collector.submitCustomGeometry(poseStack, GHOST, (pose, buffer) -> {
				QuadInstance instance = new QuadInstance();
				for (BlockStateModelPart part : parts) {
					emit(buffer, pose, instance, part.getQuads(null), a);
					for (Direction d : Direction.values())
						emit(buffer, pose, instance, part.getQuads(d), a);
				}
			});
			poseStack.popPose();
		}
	}

	private static void emit(VertexConsumer buffer, PoseStack.Pose pose, QuadInstance instance, List<BakedQuad> quads, int alpha) {
		for (BakedQuad quad : quads) {
			float shade = switch (quad.direction()) {
				case UP -> 1.0F;
				case DOWN -> 0.55F;
				case NORTH, SOUTH -> 0.82F;
				case EAST, WEST -> 0.68F;
			};
			int c = Math.round(255 * shade);
			instance.setColor(ARGB.color(alpha, c, c, c));
			buffer.putBakedQuad(pose, quad, instance);
		}
	}
}
