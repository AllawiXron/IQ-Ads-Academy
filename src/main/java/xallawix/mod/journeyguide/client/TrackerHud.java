package xallawix.mod.journeyguide.client;

import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.build.BuildCheck;
import xallawix.mod.journeyguide.journey.Journey;
import xallawix.mod.journeyguide.journey.Step;
import xallawix.mod.journeyguide.network.SyncPacket;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TrackerHud {
    private static final int COMPACT_W = 168;
    private static final int DETAILED_W = 196;
    private static final int PAD = 4;
    private static final int TEXT_X = PAD + 19;
    private static final long FRESH_TIP_MS = 20_000;

    static final int GOLD = 0xFFFFD34D;
    static final int WHITE = 0xFFFFFFFF;
    static final int GRAY = 0xFFB8B8B8;
    static final int DIM = 0xFF808080;
    static final int TIP = 0xFFE8E0A0;
    static final int GREEN = 0xFF6EE06E;
    static final int RED = 0xFFFF6B6B;
    static final int AQUA = 0xFF6FE3FF;
    static final int ORANGE = 0xFFFFA020;

    private static String lastTipKey = "";
    private static long tipShownAt;

    private TrackerHud() {}

    public static void extract(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !ClientState.received() || mc.gui.screen() != null && !mc.gui.screen().isInGameUi())
            return;
        if (mc.getDebugOverlay().showDebugScreen())
            return;

        ClientConfig.TrackerMode mode = ClientConfig.mode();
        Font font = mc.font;
        if (PlacementMode.active())
            drawPlacement(g, font);
        else if (mode == ClientConfig.TrackerMode.HIDDEN)
            return;
        else if (BuildView.active())
            drawBuild(g, font, player, mode == ClientConfig.TrackerMode.DETAILED);
        else if (mode == ClientConfig.TrackerMode.DETAILED)
            drawStepDetailed(g, font, player);
        else
            drawStepCompact(g, font, player);
    }

    private static void drawStepCompact(GuiGraphicsExtractor g, Font font, LocalPlayer player) {
        Step step = ClientState.currentStep();
        if (step == null) {
            drawFinished(g, font, COMPACT_W);
            return;
        }
        int textW = COMPACT_W - TEXT_X - PAD;
        int target = step.goal().target();
        int progress = ClientState.progress(step);
        String count = progress + "/" + target;

        List<FormattedCharSequence> summary = font.split(Component.literal(step.summary()), textW - font.width(count) - 4);
        if (summary.size() > 2)
            summary = summary.subList(0, 2);
        String extra = extraLine(step, player, false);
        List<FormattedCharSequence> tip = freshTip(step, font, textW);

        int height = PAD + summary.size() * 9 + 5 + (extra == null ? 0 : 10) + (tip.isEmpty() ? 0 : 2 + tip.size() * 9) + PAD;
        height = Math.max(height, 24);
        int x = left(COMPACT_W);
        int y = 4;
        panel(g, x, y, COMPACT_W, height);

        g.item(step.iconStack(), x + PAD, y + (Math.min(height, 32) - 16) / 2);
        int ty = y + PAD;
        g.text(font, count, x + COMPACT_W - PAD - font.width(count), ty, GRAY, false);
        for (FormattedCharSequence line : summary) {
            g.text(font, line, x + TEXT_X, ty, WHITE, false);
            ty += 9;
        }
        bar(g, x + TEXT_X, ty + 1, textW, 3, progress, target);
        ty += 5;
        if (extra != null) {
            ty += 1;
            g.text(font, extra, x + TEXT_X, ty, AQUA, false);
            ty += 9;
        }
        if (!tip.isEmpty()) {
            ty += 2;
            for (FormattedCharSequence line : tip) {
                g.text(font, line, x + TEXT_X, ty, TIP, false);
                ty += 9;
            }
        }
    }

    private static List<FormattedCharSequence> freshTip(Step step, Font font, int width) {
        int visible = ClientState.visibleTips(step);
        String key = step.id() + ":" + visible;
        long now = Util.getMillis();
        if (!key.equals(lastTipKey)) {
            lastTipKey = key;
            tipShownAt = now;
        }
        if (visible <= 0 || now - tipShownAt > FRESH_TIP_MS)
            return List.of();
        List<FormattedCharSequence> lines = font.split(Component.literal(step.tips().get(visible - 1)), width);
        return lines.size() > 4 ? lines.subList(0, 4) : lines;
    }

    private static void drawStepDetailed(GuiGraphicsExtractor g, Font font, LocalPlayer player) {
        Step step = ClientState.currentStep();
        if (step == null) {
            drawFinished(g, font, DETAILED_W);
            return;
        }
        int textW = DETAILED_W - TEXT_X - PAD;
        List<FormattedCharSequence> summary = font.split(Component.literal(step.summary()), textW);
        int tipIndex = ClientState.visibleTips(step) - 1;
        List<FormattedCharSequence> tip = tipIndex < 0 ? List.of()
                : font.split(Component.literal(step.tips().get(tipIndex)), textW - 8);
        if (tip.size() > 3)
            tip = tip.subList(0, 3);

        String extra = extraLine(step, player, true);
        int height = PAD + 10 + 10 + summary.size() * 9 + 8 + (tip.isEmpty() ? 0 : 3 + tip.size() * 9)
                + (extra == null ? 0 : 11) + 11 + PAD;
        int x = left(DETAILED_W);
        int y = 4;
        panel(g, x, y, DETAILED_W, height);

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
        bar(g, x + TEXT_X, ty + 1, textW - 34, 5, progress, target);
        g.text(font, progress + "/" + target, x + TEXT_X + textW - 30, ty, WHITE, false);
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
        g.text(font, "J: journal   H: smaller / hide", x + TEXT_X, ty, DIM, false);
    }

    private static void drawFinished(GuiGraphicsExtractor g, Font font, int width) {
        int x = left(width);
        panel(g, x, 4, width, 24);
        g.text(font, "Journey complete!", x + PAD, 4 + PAD, GOLD, true);
        g.text(font, "Press J to look back", x + PAD, 4 + PAD + 10, GRAY, false);
    }

    private static String extraLine(Step step, LocalPlayer player, boolean detailed) {
        SyncPacket.Pointer pointer = ClientState.pointer();
        if (pointer != null) {
            if (!player.level().dimension().identifier().equals(pointer.dimension()))
                return pointer.name() + ": other dimension";
            double dx = pointer.pos().getX() + 0.5 - player.getX();
            double dz = pointer.pos().getZ() + 0.5 - player.getZ();
            int dist = (int) Math.sqrt(dx * dx + dz * dz);
            return arrow(player, dx, dz) + " " + pointer.name() + " " + dist + "m";
        }
        if (step.bestY() != null)
            return "Y " + player.getBlockY() + "  (best: Y " + step.bestY() + ")";
        int untilPointer = ClientState.ticksUntilPointer(step);
        if (detailed && untilPointer > 0)
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

    static String arrow(LocalPlayer player, double dx, double dz) {
        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
        double diff = Mth.wrapDegrees(targetYaw - player.getYRot());
        String[] arrows = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};
        int sector = Math.floorMod((int) Math.round(diff / 45.0), 8);
        return arrows[sector];
    }

    private static void drawPlacement(GuiGraphicsExtractor g, Font font) {
        Blueprint bp = PlacementMode.blueprint();
        if (bp == null)
            return;
        int x = left(COMPACT_W);
        int y = 4;
        panel(g, x, y, COMPACT_W, PAD * 2 + 28);
        g.item(bp.icon.getDefaultInstance(), x + PAD, y + PAD + 4);
        int ty = y + PAD;
        g.text(font, "Placing: " + bp.name, x + TEXT_X, ty, GOLD, false);
        ty += 10;
        g.text(font, "R rotate  ↑↓ height", x + TEXT_X, ty, AQUA, false);
        ty += 9;
        g.text(font, "Enter place  ⌫ cancel", x + TEXT_X, ty, AQUA, false);
    }

    private static void drawBuild(GuiGraphicsExtractor g, Font font, LocalPlayer player, boolean detailed) {
        Blueprint bp = BuildView.blueprint();
        if (bp == null)
            return;
        int width = detailed ? DETAILED_W : COMPACT_W;
        int textW = width - TEXT_X - PAD;
        BuildView.CellView target = BuildView.target();
        String targetLine = target == null ? null : targetLine(player, target);
        int targetColor = target == null ? AQUA : targetColor(player, target);
        String problems = problemsLine();

        Map<Block, Integer> needs = BuildView.layerNeeds();
        int materialRows = needs.isEmpty() ? 0 : 1;
        int cursor = 0;
        for (Map.Entry<Block, Integer> e : needs.entrySet()) {
            int w = 18 + font.width(countText(player, e.getKey(), e.getValue())) + 4;
            if (cursor + w > textW) {
                materialRows++;
                cursor = 0;
            }
            cursor += w;
        }
        materialRows = Math.min(materialRows, 2);

        int height = PAD + 9 + 9 + materialRows * 17 + (targetLine == null ? 0 : 10) + (problems == null ? 0 : 10)
                + (detailed && targetLine == null ? 10 : 0) + PAD;
        int x = left(width);
        int y = 4;
        panel(g, x, y, width, height);

        g.item(bp.icon.getDefaultInstance(), x + PAD, y + PAD);
        int ty = y + PAD;
        g.text(font, font.plainSubstrByWidth(bp.name, textW), x + TEXT_X, ty, GOLD, false);
        ty += 9;
        g.text(font, "Layer " + (BuildView.currentLayer() + 1) + "/" + bp.height + "  ·  " + BuildView.remainingInLayer() + " left",
                x + TEXT_X, ty, GRAY, false);
        ty += 10;

        int mx = x + TEXT_X;
        int row = 0;
        for (Map.Entry<Block, Integer> e : needs.entrySet()) {
            String text = countText(player, e.getKey(), e.getValue());
            int w = 18 + font.width(text) + 4;
            if (mx + w > x + TEXT_X + textW) {
                if (++row >= materialRows)
                    break;
                mx = x + TEXT_X;
                ty += 17;
            }
            g.item(BuildCheck.itemFor(e.getKey()).getDefaultInstance(), mx, ty);
            g.text(font, text, mx + 18, ty + 4, countColor(player, e.getKey(), e.getValue()), false);
            mx += w;
        }
        if (materialRows > 0)
            ty += 17;

        if (targetLine != null) {
            g.text(font, font.plainSubstrByWidth(targetLine, width - 2 * PAD), x + PAD, ty, targetColor, false);
            ty += 10;
        } else if (detailed) {
            g.text(font, "Aim at a see-through block", x + PAD, ty, DIM, false);
            ty += 10;
        }
        if (problems != null)
            g.text(font, problems, x + PAD, ty, problems.startsWith("!") ? RED : ORANGE, false);
    }

    private static String countText(LocalPlayer player, Block block, int need) {
        if (!BuildCheck.countsInventory(block))
            return "x" + need;
        return BuildView.carried(player, block) + "/" + need;
    }

    private static int countColor(LocalPlayer player, Block block, int need) {
        if (!BuildCheck.countsInventory(block))
            return GRAY;
        return BuildView.carried(player, block) >= need ? GREEN : RED;
    }

    private static String targetLine(LocalPlayer player, BuildView.CellView target) {
        Block block = target.cell().state().getBlock();
        String name = block.getName().getString();
        if (target.ground())
            return "▶ " + name + ": dig out the ground here first";
        String note = BuildCheck.placementNote(block);
        if (!note.isEmpty() && !(BuildCheck.itemFor(block) instanceof BlockItem))
            return "▶ " + name + ": " + note;
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (EasyPlace.holdsBlockFor(main, target.cell().state()) || EasyPlace.holdsBlockFor(off, target.cell().state()))
            return EasyPlace.placeablePos(player) != null ? "▶ Right-click to place " + name : "▶ " + name + ": move closer";
        if (main.getItem() instanceof BlockItem)
            return "▶ Needs " + name + ", not " + main.getHoverName().getString();
        return "▶ Needs " + name;
    }

    private static int targetColor(LocalPlayer player, BuildView.CellView target) {
        if (target.ground())
            return ORANGE;
        ItemStack main = player.getMainHandItem();
        if (EasyPlace.holdsBlockFor(main, target.cell().state()) || EasyPlace.holdsBlockFor(player.getOffhandItem(), target.cell().state()))
            return GREEN;
        return main.getItem() instanceof BlockItem ? RED : AQUA;
    }

    private static String problemsLine() {
        int wrong = BuildView.wrongCount();
        int ground = BuildView.groundCount();
        if (wrong > 0 && ground > 0)
            return "! " + wrong + " wrong (red), " + ground + " to dig out";
        if (wrong > 0)
            return "! " + wrong + " wrong block" + (wrong > 1 ? "s" : "") + " (red): break them";
        if (ground > 0)
            return ground + " ground block" + (ground > 1 ? "s" : "") + " to dig out (orange)";
        return null;
    }

    private static int left(int width) {
        if (ClientConfig.corner() == ClientConfig.Corner.TOP_RIGHT)
            return Minecraft.getInstance().getWindow().getGuiScaledWidth() - width - 4;
        return 4;
    }

    private static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height) {
        g.fill(x, y, x + width, y + height, 0x90101018);
    }

    static void bar(GuiGraphicsExtractor g, int x, int y, int width, int height, int value, int max) {
        g.fill(x, y, x + width, y + height, 0xFF505050);
        int filled = max <= 0 ? 0 : (int) ((long) width * Math.min(value, max) / max);
        if (filled > 0)
            g.fill(x, y, x + filled, y + height, value >= max ? 0xFF4CC24C : 0xFFE0B030);
    }

    static String time(int ticks) {
        int seconds = ticks / 20;
        return String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);
    }
}
