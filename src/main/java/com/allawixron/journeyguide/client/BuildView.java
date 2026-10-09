package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.build.Blueprint;
import com.allawixron.journeyguide.build.Blueprints;
import com.allawixron.journeyguide.build.BuildCheck;
import com.allawixron.journeyguide.network.SyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Client-side picture of the build in progress: which blocks are done, missing or wrong. */
public final class BuildView {
    private static final int REFRESH_TICKS = 5;

    /** {@code ground}: natural ground in a layer that sits in the ground (floors, farmland) that has to be dug out. */
    public record CellView(Blueprint.Cell cell, BuildCheck.Status status, AABB box, boolean ground) {}

    private static @Nullable Blueprint blueprint;
    private static List<CellView> cells = List.of();
    private static int currentLayer;
    private static int remainingInLayer;
    private static int remainingTotal;
    private static int wrongCount;
    private static int groundCount;
    private static Map<Block, Integer> layerNeeds = Map.of();
    private static Map<Block, Integer> totalNeeds = Map.of();
    private static @Nullable CellView target;
    private static boolean dirty = true;
    private static int ticks;

    private BuildView() {}

    public static void invalidate() {
        dirty = true;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            clear();
            return;
        }
        if (dirty || ++ticks >= REFRESH_TICKS) {
            ticks = 0;
            dirty = false;
            refresh(mc.level);
        }
        target = findTarget(mc.player);
    }

    private static void clear() {
        blueprint = null;
        cells = List.of();
        target = null;
        layerNeeds = Map.of();
        totalNeeds = Map.of();
    }

    private static void refresh(ClientLevel level) {
        SyncPacket.Build build = ClientState.build();
        Blueprint bp = build == null ? null : Blueprints.get(build.blueprintId());
        if (bp == null || !level.dimension().identifier().equals(build.dimension())) {
            clear();
            return;
        }
        blueprint = bp;

        List<CellView> views = new ArrayList<>();
        int lowestUnfinished = Integer.MAX_VALUE;
        int remaining = 0;
        int wrong = 0;
        int ground = 0;
        for (Blueprint.Cell cell : bp.cells(build.origin(), build.rotation())) {
            BuildCheck.Status status = BuildCheck.status(level.getBlockState(cell.pos()), cell.state());
            boolean isGround = status == BuildCheck.Status.WRONG && cell.layer() < bp.sink;
            views.add(new CellView(cell, status, shapeOf(level, cell), isGround));
            if (!cell.mustBeEmpty() && status != BuildCheck.Status.DONE) {
                lowestUnfinished = Math.min(lowestUnfinished, cell.layer());
                if (BuildCheck.materialOf(cell.state()) != net.minecraft.world.level.block.Blocks.AIR)
                    remaining++;
                if (isGround)
                    ground++;
                else if (status == BuildCheck.Status.WRONG)
                    wrong++;
            }
        }
        cells = views;
        currentLayer = lowestUnfinished == Integer.MAX_VALUE ? bp.height - 1 : lowestUnfinished;
        remainingTotal = remaining;
        wrongCount = wrong;
        groundCount = ground;

        Map<Block, Integer> layer = new HashMap<>();
        Map<Block, Integer> total = new HashMap<>();
        int inLayer = 0;
        for (CellView v : views) {
            if (v.cell.mustBeEmpty() || v.status == BuildCheck.Status.DONE)
                continue;
            Block material = BuildCheck.materialOf(v.cell.state());
            if (material == net.minecraft.world.level.block.Blocks.AIR)
                continue;
            total.merge(material, 1, Integer::sum);
            if (v.cell.layer() == currentLayer) {
                layer.merge(material, 1, Integer::sum);
                inLayer++;
            }
        }
        remainingInLayer = inLayer;
        layerNeeds = Blueprint.sortByCount(layer);
        totalNeeds = Blueprint.sortByCount(total);
    }

    private static AABB shapeOf(ClientLevel level, Blueprint.Cell cell) {
        if (cell.mustBeEmpty())
            return new AABB(cell.pos());
        VoxelShape shape = cell.state().getShape(level, cell.pos());
        if (shape.isEmpty())
            return new AABB(cell.pos()).deflate(0.3);
        return shape.bounds().move(cell.pos());
    }

    /** The unfinished hologram block in the current layer that the player is looking at. */
    private static @Nullable CellView findTarget(LocalPlayer player) {
        if (cells.isEmpty())
            return null;
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(8.0));
        CellView best = null;
        double bestDist = Double.MAX_VALUE;
        for (CellView v : cells) {
            if (v.cell.layer() != currentLayer || v.cell.mustBeEmpty() || v.status == BuildCheck.Status.DONE)
                continue;
            Optional<Vec3> hit = new AABB(v.cell.pos()).clip(eye, end);
            if (hit.isPresent()) {
                double d = hit.get().distanceToSqr(eye);
                if (d < bestDist) {
                    bestDist = d;
                    best = v;
                }
            }
        }
        return best;
    }

    /** How many of this block's item the player is carrying (any bed counts for a bed). */
    public static int carried(LocalPlayer player, Block block) {
        Item wanted = BuildCheck.itemFor(block);
        boolean anyBed = block instanceof BedBlock;
        Inventory inv = player.getInventory();
        int count = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty())
                continue;
            if (stack.getItem() == wanted || (anyBed && stack.is(ItemTags.BEDS)))
                count += stack.getCount();
        }
        return count;
    }

    // ------------------------------------------------------------------ getters

    public static boolean active() {
        return blueprint != null;
    }

    public static @Nullable Blueprint blueprint() {
        return blueprint;
    }

    public static List<CellView> cells() {
        return cells;
    }

    public static int currentLayer() {
        return currentLayer;
    }

    public static int remainingInLayer() {
        return remainingInLayer;
    }

    public static int remainingTotal() {
        return remainingTotal;
    }

    public static int wrongCount() {
        return wrongCount;
    }

    public static int groundCount() {
        return groundCount;
    }

    public static Map<Block, Integer> layerNeeds() {
        return layerNeeds;
    }

    public static Map<Block, Integer> totalNeeds() {
        return totalNeeds;
    }

    public static @Nullable CellView target() {
        return target;
    }
}
