package xallawix.mod.journeyguide.client.renderer;

import xallawix.mod.journeyguide.client.ClientState;
import xallawix.mod.journeyguide.client.BuildView;
import xallawix.mod.journeyguide.client.Holograms;
import xallawix.mod.journeyguide.client.PlacementMode;

import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.build.BuildCheck;
import xallawix.mod.journeyguide.network.JourneySyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GhostBlockRenderer {
	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent.Pre event) {
		render();
	}

	private static final int HOLO = 0x9FD8FF;
	private static final int WRONG = 0xFF4040;
	private static final int OBSTRUCTED = 0xFFA020;


	public static void render() {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level == null || mc.player == null || mc.gui.hud.isHidden())
			return;

		float pulse = 0.5F + 0.5F * (float) Math.sin(Util.getMillis() / 250.0);

		if (PlacementMode.active()) {
			for (Blueprint.Cell cell : PlacementMode.cells())
				if (!cell.mustBeEmpty() && !Holograms.hasModel(cell.state()))
					Gizmos.cuboid(new AABB(cell.pos()).deflate(0.06), GizmoStyle.fill(ARGB.color(80, tint(level, cell.state(), cell))));
		} else if (BuildView.active()) {
			renderBuild(level, pulse);
		}

		renderPointer(level);
	}

	private static void renderBuild(ClientLevel level, float pulse) {
		int layer = BuildView.currentLayer();
		BuildView.CellView target = BuildView.target();

		for (BuildView.CellView v : BuildView.cells()) {
			Blueprint.Cell cell = v.cell();
			switch (v.status()) {
				case DONE -> {
				}
				case OBSTRUCTED -> {
					if (cell.layer() <= layer + 1)
						Gizmos.cuboid(new AABB(cell.pos()).deflate(0.02), GizmoStyle.stroke(ARGB.color(200, OBSTRUCTED), 2.0F));
				}
				case WRONG -> {
					if (v.ground()) {
						if (cell.layer() == layer)
							Gizmos.cuboid(new AABB(cell.pos()).inflate(0.005), GizmoStyle.stroke(ARGB.color(200, OBSTRUCTED), 1.5F));
						continue;
					}
					int alpha = cell.layer() == layer ? 90 : 35;
					Gizmos.cuboid(new AABB(cell.pos()).inflate(0.01),
							GizmoStyle.strokeAndFill(ARGB.color(230, WRONG), 2.5F, ARGB.color(alpha, WRONG)));
				}
				case MISSING -> {
					if (cell.layer() == layer && !Holograms.hasModel(cell.state()))
						Gizmos.cuboid(v.box().deflate(0.04), GizmoStyle.fill(ARGB.color((int) (70 + 40 * pulse), tint(level, cell.state(), cell))));
				}
			}
		}

		if (target != null) {
			Blueprint.Cell cell = target.cell();
			Gizmos.cuboid(new AABB(cell.pos()).inflate(0.002), GizmoStyle.stroke(ARGB.color(230, 0xFFFFFF), 2.5F));
			String name = cell.state().getBlock().getName().getString();
			Vec3 above = Vec3.atBottomCenterOf(cell.pos()).add(0, 1.15, 0);
			Gizmos.billboardText(name, above, TextGizmo.Style.forColorAndCentered(0xFFFFFFFF).withScale(0.4F)).setAlwaysOnTop();
		}
	}

	private static void renderPointer(ClientLevel level) {
		JourneySyncMessage.Pointer pointer = ClientState.pointer();
		if (pointer == null || !level.dimension().identifier().equals(pointer.dimension()))
			return;
		Vec3 bottom = new Vec3(pointer.pos().getX() + 0.5, level.getMinY(), pointer.pos().getZ() + 0.5);
		Vec3 top = new Vec3(bottom.x, level.getMaxY(), bottom.z);
		Gizmos.line(bottom, top, ARGB.color(200, 0xFFD34D), 4.0F);
	}

	private static int tint(ClientLevel level, BlockState state, Blueprint.Cell cell) {
		MapColor color = state.getMapColor(level, cell.pos());
		int rgb = color == null ? 0 : color.col;
		if (rgb == 0)
			return HOLO;
		return ARGB.srgbLerp(0.35F, rgb, HOLO) & 0xFFFFFF;
	}
}
