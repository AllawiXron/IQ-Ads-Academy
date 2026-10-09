package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.ModEntities;
import com.allawixron.journeyguide.build.Blueprint;
import com.allawixron.journeyguide.build.BuildCheck;
import com.allawixron.journeyguide.build.HologramAnchor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decides which see-through blocks to draw this tick (the placement preview, or the current layer of the
 * build plus a faint look at the next layers) and keeps the invisible anchor entity that draws them alive.
 */
public final class Holograms {
    /** A see-through block. {@code pulse}: gently breathes so it's easy to spot. */
    public record Ghost(BlockPos pos, BlockState state, float alpha, boolean pulse) {}

    private static List<Ghost> ghosts = List.of();
    private static final Map<BlockState, Boolean> HAS_MODEL = new HashMap<>();
    private static @Nullable HologramAnchor anchor;
    private static int nextId = -0x4A47_0000;

    private Holograms() {}

    public static List<Ghost> ghosts() {
        return ghosts;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            ghosts = List.of();
            anchor = null;
            return;
        }

        List<Ghost> list = new ArrayList<>();
        if (PlacementMode.active()) {
            for (Blueprint.Cell cell : PlacementMode.cells())
                if (!cell.mustBeEmpty() && hasModel(cell.state()))
                    list.add(new Ghost(cell.pos(), cell.state(), 0.38F, false));
        } else if (BuildView.active()) {
            int layer = BuildView.currentLayer();
            BuildView.CellView target = BuildView.target();
            for (BuildView.CellView v : BuildView.cells()) {
                Blueprint.Cell cell = v.cell();
                if (cell.mustBeEmpty() || !hasModel(cell.state()))
                    continue;
                boolean missing = v.status() == BuildCheck.Status.MISSING;
                if (cell.layer() == layer && (missing || v.ground())) {
                    boolean looked = target != null && target.cell().pos().equals(cell.pos());
                    list.add(new Ghost(cell.pos(), cell.state(), looked ? 0.8F : 0.5F, !looked));
                } else if (missing && cell.layer() > layer && cell.layer() <= layer + 2) {
                    list.add(new Ghost(cell.pos(), cell.state(), cell.layer() == layer + 1 ? 0.16F : 0.08F, false));
                }
            }
        }
        ghosts = list;

        keepAnchor(mc, player, !list.isEmpty());
    }

    /** Chests, beds and water have no plain block model; those are drawn as tinted boxes instead. */
    public static boolean hasModel(BlockState state) {
        return HAS_MODEL.computeIfAbsent(state, s -> {
            BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(s);
            List<BlockStateModelPart> parts = new ArrayList<>();
            model.collectParts(RandomSource.create(42L), parts);
            for (BlockStateModelPart part : parts) {
                if (!part.getQuads(null).isEmpty())
                    return true;
                for (Direction d : Direction.values())
                    if (!part.getQuads(d).isEmpty())
                        return true;
            }
            return false;
        });
    }

    private static void keepAnchor(Minecraft mc, LocalPlayer player, boolean needed) {
        if (!needed) {
            if (anchor != null && !anchor.isRemoved())
                anchor.discard();
            anchor = null;
            return;
        }
        if (anchor == null || anchor.isRemoved() || anchor.level() != mc.level) {
            anchor = new HologramAnchor(ModEntities.HOLOGRAM_ANCHOR.get(), mc.level);
            anchor.setId(nextId--);
            anchor.setPos(player.getX(), player.getY(), player.getZ());
            mc.level.addEntity(anchor);
        }
        // stay next to the player so the anchor's chunk section is always visible
        anchor.setPos(player.getX(), player.getY(), player.getZ());
    }

    public static void reset() {
        ghosts = List.of();
        anchor = null;
        HAS_MODEL.clear();
    }
}
