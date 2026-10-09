package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.build.Blueprint;
import com.allawixron.journeyguide.build.BuildCheck;
import com.allawixron.journeyguide.journey.Journey;
import com.allawixron.journeyguide.journey.Step;
import com.allawixron.journeyguide.network.SyncPacket;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/** The always-visible panel in the top left: current step, progress, tips, pointer and build status. */
public final class TrackerHud {
    private static final int WIDTH = 200;
    private static final int PAD = 5;
    private static final int TEXT_X = PAD + 20;
    private static final int TEXT_W = WIDTH - TEXT_X - PAD;

    static final int GOLD = 0xFFFFD34D;
    static final int WHITE = 0xFFFFFFFF;
    static final int GRAY = 0xFFB8B8B8;
    static final int TIP = 0xFFE8E0A0;
    static final int GREEN = 0xFF6EE06E;
    static final int RED = 0xFFFF6B6B;
    static final int AQUA = 0xFF6FE3FF;

    public static boolean visible = true;

    private TrackerHud() {}

    public static void extract(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (!visible || player == null || !ClientState.received() || mc.gui.screen() != null && !mc.gui.screen().isInGameUi())
            return;
        if (mc.getDebugOverlay().showDebugScreen())
            return;

        Font font = mc.font;
        if (PlacementMode.active())
            drawPlacement(g, font);
        else if (BuildView.active())
            drawBuild(g, font, player);
        else
            drawStep(g, font, player);
    }

    // ------------------------------------------------------------------ journey step

    private static void drawStep(GuiGraphicsExtractor g, Font font, LocalPlayer player) {
        Step step = ClientState.currentStep();
        int x = 4;
        int y = 4;

        if (step == null) {
            panel(g, x, y, 26);
            g.text(font, "Journey complete!", x + PAD, y + PAD, GOLD, true);
            g.text(font, "You beat Minecraft. Press J to look back.", x + PAD, y + PAD + 10, GRAY, false);
            return;
        }

        List<FormattedCharSequence> summary = font.split(Component.literal(step.summary()), TEXT_W);
        int tipIndex = ClientState.visibleTips(step) - 1;
        List<FormattedCharSequence> tip = tipIndex < 0 ? List.of()
                : font.split(Component.literal(step.tips().get(tipIndex)), TEXT_W - 8);
        if (tip.size() > 3)
            tip = tip.subList(0, 3);

        String extra = extraLine(step, player);
        int height = PAD + 10 + 10 + summary.size() * 9 + 8 + (tip.isEmpty() ? 0 : 3 + tip.size() * 9)
                + (extra == null ? 0 : 11) + 11 + PAD;
        panel(g, x, y, height);

        int index = Journey.indexOf(step) + 1;
        g.item(step.iconStack(), x + PAD, y + PAD + 2);
        int ty = y + PAD;
        g.text(font, "Step " + index + "/" + Journey.steps().size() + "  ·  " + step.chapter().title, x + TEXT_X, ty, GOLD, false);
        ty += 10;
        g.text(font, step.title(), x + TEXT_X, ty, WHITE, true);
        ty += 10;
        for (FormattedCharSequence line : summary) {
            g.text(font, line, x + TEXT_X, ty, GRAY, false);
            ty += 9;
        }

        int target = step.goal().target();
        int progress = ClientState.progress(step);
        bar(g, x + TEXT_X, ty + 1, TEXT_W - 34, progress, target);
        g.text(font, progress + "/" + target, x + TEXT_X + TEXT_W - 30, ty, WHITE, false);
        ty += 8;

        if (!tip.isEmpty()) {
            ty += 3;
            g.text(font, "✱", x + TEXT_X, ty, TIP, false);
            for (FormattedCharSequence line : tip) {
                g.text(font, line, x + TEXT_X + 8, ty, TIP, false);
                ty += 9;
            }
        }

        if (extra != null) {
            ty += 2;
            g.text(font, extra, x + TEXT_X, ty, AQUA, false);
            ty += 9;
        }

        ty += 2;
        g.text(font, "J: journal" + (step.blueprint() != null ? " + build plan" : "") + "   H: hide", x + TEXT_X, ty, 0xFF808080, false);
    }

    /** Pointer, best height or "unlocks in" line for the current step. */
    private static String extraLine(Step step, LocalPlayer player) {
        SyncPacket.Pointer pointer = ClientState.pointer();
        if (pointer != null) {
            if (!player.level().dimension().identifier().equals(pointer.dimension()))
                return pointer.name() + ": in another dimension";
            double dx = pointer.pos().getX() + 0.5 - player.getX();
            double dz = pointer.pos().getZ() + 0.5 - player.getZ();
            int dist = (int) Math.sqrt(dx * dx + dz * dz);
            return arrow(player, dx, dz) + " " + pointer.name() + ": " + dist + " blocks";
        }
        if (step.bestY() != null)
            return "Your height: Y " + player.getBlockY() + "   (best: Y " + step.bestY() + ")";
        int untilPointer = ClientState.ticksUntilPointer(step);
        if (untilPointer > 0)
            return "Stuck? A pointer unlocks in " + time(untilPointer);
        if (step.locate() != null && untilPointer == 0 && step.locate().dimension() != player.level().dimension())
            return "Go to the " + dimensionName(step.locate().dimension().identifier().getPath()) + " first";
        return null;
    }

    private static String dimensionName(String path) {
        return switch (path) {
            case "the_nether" -> "Nether";
            case "the_end" -> "End";
            default -> "Overworld";
        };
    }

    /** An arrow showing which way to turn to face the target. */
    static String arrow(LocalPlayer player, double dx, double dz) {
        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
        double diff = Mth.wrapDegrees(targetYaw - player.getYRot());
        String[] arrows = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};
        int sector = Math.floorMod((int) Math.round(diff / 45.0), 8);
        return arrows[sector];
    }

    // ------------------------------------------------------------------ building

    private static void drawPlacement(GuiGraphicsExtractor g, Font font) {
        Blueprint bp = PlacementMode.blueprint();
        if (bp == null)
            return;
        int x = 4;
        int y = 4;
        panel(g, x, y, PAD * 2 + 10 + 4 * 10);
        g.item(bp.icon.getDefaultInstance(), x + PAD, y + PAD + 2);
        int ty = y + PAD;
        g.text(font, "Placing: " + bp.name, x + TEXT_X, ty, GOLD, true);
        ty += 12;
        g.text(font, "Look where it should go", x + TEXT_X, ty, WHITE, false);
        ty += 10;
        g.text(font, "R rotate   ↑/↓ move up/down", x + TEXT_X, ty, AQUA, false);
        ty += 10;
        g.text(font, "Enter place   Backspace cancel", x + TEXT_X, ty, AQUA, false);
    }

    private static void drawBuild(GuiGraphicsExtractor g, Font font, LocalPlayer player) {
        Blueprint bp = BuildView.blueprint();
        if (bp == null)
            return;
        Map<Block, Integer> needs = BuildView.layerNeeds();
        int rows = Math.min(needs.size(), 5);
        BuildView.CellView target = BuildView.target();
        String targetHint = target == null ? "" : BlockHints.placement(target.cell().state());

        int height = PAD + 10 + 10 + 10 + 4 + rows * 18 + 4 + 10 + (target != null && !targetHint.isEmpty() ? 10 : 0)
                + (BuildView.wrongCount() > 0 ? 10 : 0) + (BuildView.groundCount() > 0 ? 10 : 0) + PAD;
        int x = 4;
        int y = 4;
        panel(g, x, y, height);

        g.item(bp.icon.getDefaultInstance(), x + PAD, y + PAD + 2);
        int ty = y + PAD;
        g.text(font, "Building: " + bp.name, x + TEXT_X, ty, GOLD, true);
        ty += 10;
        g.text(font, "Layer " + (BuildView.currentLayer() + 1) + " of " + bp.height, x + TEXT_X, ty, WHITE, false);
        ty += 10;
        g.text(font, BuildView.remainingInLayer() + " left on layer, " + BuildView.remainingTotal() + " in total",
                x + TEXT_X, ty, GRAY, false);
        ty += 12;

        int i = 0;
        for (Map.Entry<Block, Integer> e : needs.entrySet()) {
            if (i++ >= rows)
                break;
            Block block = e.getKey();
            int need = e.getValue();
            g.item(BuildCheck.itemFor(block).getDefaultInstance(), x + TEXT_X, ty);
            String name = block.getName().getString();
            String note = BuildCheck.placementNote(block);
            if (BuildCheck.countsInventory(block)) {
                int have = BuildView.carried(player, block);
                g.text(font, name, x + TEXT_X + 20, ty + 4, WHITE, false);
                String count = have + "/" + need;
                g.text(font, count, x + WIDTH - PAD - font.width(count), ty + 4, have >= need ? GREEN : RED, false);
            } else {
                g.text(font, name + " x" + need, x + TEXT_X + 20, ty, WHITE, false);
                g.text(font, note, x + TEXT_X + 20, ty + 9, GRAY, false);
            }
            ty += 18;
        }
        ty += 4;

        int lx = x + PAD;
        if (target != null) {
            g.text(font, "▶ " + target.cell().state().getBlock().getName().getString(), lx, ty, AQUA, false);
            ty += 10;
            if (!targetHint.isEmpty()) {
                g.text(font, targetHint + " (you: " + player.getDirection().getName().toLowerCase(Locale.ROOT) + ")",
                        lx + 8, ty, TIP, false);
                ty += 10;
            }
        } else {
            g.text(font, "Look at a glowing block for details", lx, ty, 0xFF808080, false);
            ty += 10;
        }
        if (BuildView.groundCount() > 0) {
            g.text(font, BuildView.groundCount() + " ground blocks to dig out (orange)", lx, ty, 0xFFFFA020, false);
            ty += 10;
        }
        if (BuildView.wrongCount() > 0)
            g.text(font, BuildView.wrongCount() + " wrong blocks (red): break them", lx, ty, RED, false);
    }

    // ------------------------------------------------------------------ helpers

    private static void panel(GuiGraphicsExtractor g, int x, int y, int height) {
        g.fill(x, y, x + WIDTH, y + height, 0xC0101018);
        g.outline(x, y, WIDTH, height, 0x60FFD34D);
    }

    static void bar(GuiGraphicsExtractor g, int x, int y, int width, int value, int max) {
        g.fill(x, y, x + width, y + 5, 0xFF505050);
        int filled = max <= 0 ? 0 : (int) ((long) width * Math.min(value, max) / max);
        if (filled > 0)
            g.fill(x, y, x + filled, y + 5, value >= max ? 0xFF4CC24C : 0xFFE0B030);
    }

    static String time(int ticks) {
        int seconds = ticks / 20;
        return String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
    }
}
