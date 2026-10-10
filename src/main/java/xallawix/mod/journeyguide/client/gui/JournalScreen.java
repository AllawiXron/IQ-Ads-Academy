package xallawix.mod.journeyguide.client.gui;

import xallawix.mod.journeyguide.client.ClientState;
import xallawix.mod.journeyguide.client.BuildView;
import xallawix.mod.journeyguide.client.PlacementMode;
import xallawix.mod.journeyguide.client.screens.TrackerOverlay;
import xallawix.mod.journeyguide.init.JourneyGuideModKeyMappings;

import xallawix.mod.journeyguide.build.Blueprint;
import xallawix.mod.journeyguide.build.Blueprints;
import xallawix.mod.journeyguide.build.BuildCheck;
import xallawix.mod.journeyguide.journey.Chapter;
import xallawix.mod.journeyguide.journey.Journey;
import xallawix.mod.journeyguide.journey.Step;
import xallawix.mod.journeyguide.network.JourneyActionMessage;
import xallawix.mod.journeyguide.JourneyGuideMod;
import xallawix.mod.journeyguide.network.JourneySyncMessage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JournalScreen extends Screen {
	private static final int BUILDS_TAB = Chapter.values().length;
	private static final int TAB_H = 22;
	private static final int ROW_H = 20;
	private static final int LEFT_W = 100;
	private static final int MID_W = 132;
	private static final int GAP = 6;

	private static int tab = -1;
	private static @Nullable String selectedStep;
	private static @Nullable String selectedBlueprint;

	private int px, py, pw, ph;
	private int leftX, midX, rightX, rightW, bodyY, bodyH;
	private int listScroll;
	private int detailScroll;
	private int detailContentH;

	public JournalScreen() {
		super(Component.literal("Journey Guide"));
		if (tab < 0) {
			Step current = ClientState.currentStep();
			tab = current == null ? Chapter.THE_END.ordinal() : current.chapter().ordinal();
			selectedStep = current == null ? null : current.id();
		}
	}

	public static void openForBlueprint(String blueprintId) {
		tab = BUILDS_TAB;
		selectedBlueprint = blueprintId;
	}

	@Override
	protected void init() {
		pw = Math.min(width - 16, 460);
		ph = Math.min(height - 16, 280);
		px = (width - pw) / 2;
		py = (height - ph) / 2;
		leftX = px + GAP;
		midX = leftX + LEFT_W + GAP;
		rightX = midX + MID_W + GAP;
		rightW = px + pw - GAP - rightX;
		bodyY = py + 24;
		bodyH = ph - 24 - GAP;

		if (tab == BUILDS_TAB) {
			if (selectedBlueprint == null || Blueprints.get(selectedBlueprint) == null)
				selectedBlueprint = Blueprints.all().getFirst().id;
		} else {
			Step step = selectedStep == null ? null : Journey.get(selectedStep);
			if (step == null || step.chapter().ordinal() != tab) {
				List<Step> steps = Journey.inChapter(Chapter.values()[tab]);
				Step pick = steps.getFirst();
				for (Step s : steps)
					if (!ClientState.isComplete(s)) {
						pick = s;
						break;
					}
				selectedStep = pick.id();
			}
		}
		addButtons();
	}

	private void addButtons() {
		List<Button> buttons = new ArrayList<>();
		if (tab == BUILDS_TAB) {
			Blueprint bp = Blueprints.get(selectedBlueprint);
			JourneySyncMessage.Build active = ClientState.build();
			if (bp != null) {
				if (active != null && active.blueprintId().equals(bp.id)) {
					buttons.add(Button.builder(Component.literal("Stop building"), b -> {
						JourneyGuideMod.sendToServer(JourneyActionMessage.cancelBuild());
						onClose();
					}).tooltip(Tooltip.create(Component.literal("Removes the hologram. Blocks you placed stay."))).build());
				} else {
					buttons.add(Button.builder(Component.literal(active == null ? "Place this build" : "Place (replaces current)"), b -> {
						PlacementMode.start(bp);
						onClose();
					}).tooltip(Tooltip.create(Component.literal("Shows a hologram you can move around. Press Enter to place it."))).build());
				}
			}
		} else {
			Step step = Journey.get(selectedStep);
			if (step != null) {
				if (ClientState.isSkipped(step)) {
					buttons.add(Button.builder(Component.literal("Undo skip"), b -> JourneyGuideMod.sendToServer(JourneyActionMessage.unskip(step.id()))).build());
				} else if (!ClientState.isComplete(step)) {
					boolean isCurrent = step == ClientState.currentStep();
					if (isCurrent && ClientState.visibleTips(step) < step.tips().size())
						buttons.add(Button.builder(Component.literal("Next tip"), b -> JourneyGuideMod.sendToServer(JourneyActionMessage.moreTips()))
								.tooltip(Tooltip.create(Component.literal("Show the next tip now instead of waiting."))).build());
					buttons.add(Button.builder(Component.literal("Skip step"), b -> JourneyGuideMod.sendToServer(JourneyActionMessage.skip(step.id())))
							.tooltip(Tooltip.create(Component.literal("Did it your own way? Mark this step as done."))).build());
				}
				if (step.blueprint() != null)
					buttons.add(Button.builder(Component.literal("Build plan"), b -> {
						openForBlueprint(step.blueprint());
						rebuildWidgets();
					}).build());
			}
		}

		if (buttons.isEmpty())
			return;
		int bw = (rightW - (buttons.size() - 1) * 4) / buttons.size();
		int bx = rightX;
		int by = py + ph - GAP - 20;
		for (Button b : buttons) {
			b.setRectangle(bw, 20, bx, by);
			addRenderableWidget(b);
			bx += bw + 4;
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		g.fill(px, py, px + pw, py + ph, 0xE0141418);
		g.outline(px, py, pw, ph, 0xFF6B5A2E);

		g.text(font, "Journey Guide", px + GAP, py + 8, TrackerOverlay.GOLD, true);
		String total = ClientState.completedCount() + " / " + Journey.steps().size() + " steps done";
		g.text(font, total, px + pw - GAP - font.width(total), py + 8, TrackerOverlay.GRAY, false);
		TrackerOverlay.bar(g, px + GAP + font.width("Journey Guide") + 10, py + 10,
				pw - 2 * GAP - font.width("Journey Guide") - font.width(total) - 20,
				5, ClientState.completedCount(), Journey.steps().size());

		drawTabs(g, mouseX, mouseY);
		g.fill(midX - GAP / 2, bodyY, midX - GAP / 2 + 1, bodyY + bodyH, 0x40FFFFFF);
		g.fill(rightX - GAP / 2, bodyY, rightX - GAP / 2 + 1, bodyY + bodyH, 0x40FFFFFF);

		if (tab == BUILDS_TAB) {
			drawBlueprintList(g, mouseX, mouseY);
			drawBlueprintDetails(g);
		} else {
			drawStepList(g, mouseX, mouseY);
			drawStepDetails(g);
		}

		super.extractRenderState(g, mouseX, mouseY, a);
	}

	private void drawTabs(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int y = bodyY;
		for (int i = 0; i <= BUILDS_TAB; i++) {
			boolean selected = i == tab;
			boolean hover = inside(mouseX, mouseY, leftX, y, LEFT_W, TAB_H - 2);
			g.fill(leftX, y, leftX + LEFT_W, y + TAB_H - 2, selected ? 0x806B5A2E : hover ? 0x40FFFFFF : 0x20FFFFFF);
			ItemStack icon;
			String label;
			String sub;
			int subColor = TrackerOverlay.GRAY;
			if (i == BUILDS_TAB) {
				icon = Blueprints.get("cozy_cabin").icon.getDefaultInstance();
				label = "Builds";
				sub = Blueprints.all().size() + " blueprints";
			} else {
				Chapter chapter = Chapter.values()[i];
				icon = chapter.icon();
				label = chapter.title;
				int done = ClientState.completedInChapter(chapter);
				int count = Journey.inChapter(chapter).size();
				sub = done + "/" + count;
				if (done == count) {
					sub += " ✔";
					subColor = TrackerOverlay.GREEN;
				}
			}
			g.item(icon, leftX + 2, y + 2);
			g.text(font, label, leftX + 21, y + 2, selected ? TrackerOverlay.GOLD : TrackerOverlay.WHITE, false);
			g.text(font, sub, leftX + 21, y + 11, subColor, false);
			y += TAB_H;
		}
	}

	private void drawStepList(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		List<Step> steps = Journey.inChapter(Chapter.values()[tab]);
		Step current = ClientState.currentStep();
		g.enableScissor(midX, bodyY, midX + MID_W, bodyY + bodyH);
		int y = bodyY - listScroll;
		for (Step step : steps) {
			boolean selected = step.id().equals(selectedStep);
			boolean hover = inside(mouseX, mouseY, midX, y, MID_W, ROW_H - 1) && mouseY >= bodyY && mouseY < bodyY + bodyH;
			g.fill(midX, y, midX + MID_W, y + ROW_H - 1, selected ? 0x806B5A2E : hover ? 0x40FFFFFF : 0x18FFFFFF);
			g.item(step.iconStack(), midX + 2, y + 1);
			int color = ClientState.isComplete(step) ? TrackerOverlay.GRAY : step == current ? TrackerOverlay.GOLD : TrackerOverlay.WHITE;
			String title = fit(step.title(), MID_W - 34);
			g.text(font, title, midX + 21, y + 6, color, false);
			String mark = ClientState.isSkipped(step) ? "↷" : ClientState.isComplete(step) ? "✔" : step == current ? "▶" : "";
			int markColor = ClientState.isSkipped(step) ? TrackerOverlay.GRAY : ClientState.isComplete(step) ? TrackerOverlay.GREEN : TrackerOverlay.GOLD;
			g.text(font, mark, midX + MID_W - 10, y + 6, markColor, false);
			y += ROW_H;
		}
		g.disableScissor();
	}

	private void drawStepDetails(GuiGraphicsExtractor g) {
		Step step = Journey.get(selectedStep);
		if (step == null)
			return;
		int bottom = py + ph - GAP - 24;
		g.enableScissor(rightX, bodyY, rightX + rightW, bottom);
		int y = bodyY - detailScroll;
		int top = y;

		g.item(step.iconStack(), rightX, y);
		g.text(font, step.title(), rightX + 20, y, TrackerOverlay.WHITE, true);
		String status;
		int statusColor;
		if (ClientState.isSkipped(step)) {
			status = "Skipped";
			statusColor = TrackerOverlay.GRAY;
		} else if (ClientState.isComplete(step)) {
			status = "Done ✔";
			statusColor = TrackerOverlay.GREEN;
		} else {
			status = "Progress: " + ClientState.progress(step) + "/" + step.goal().target();
			statusColor = TrackerOverlay.GOLD;
		}
		g.text(font, status, rightX + 20, y + 9, statusColor, false);
		y += 22;

		y = wrapped(g, step.summary(), rightX, y, rightW, TrackerOverlay.WHITE) + 4;
		y = wrapped(g, "Why: " + step.why(), rightX, y, rightW, TrackerOverlay.GRAY) + 6;

		g.text(font, "How to do it", rightX, y, TrackerOverlay.GOLD, false);
		y += 11;
		int visible = ClientState.visibleTips(step);
		for (int i = 0; i < visible; i++) {
			g.text(font, "✱", rightX, y, TrackerOverlay.TIP, false);
			y = wrapped(g, step.tips().get(i), rightX + 9, y, rightW - 9, TrackerOverlay.TIP) + 3;
		}
		int locked = step.tips().size() - visible;
		if (locked > 0) {
			int until = ClientState.ticksUntilNextTip(step);
			String text = locked + " more tip" + (locked > 1 ? "s" : "") + (until > 0 ? ": next unlocks in " + TrackerOverlay.time(until) : "")
					+ (step == ClientState.currentStep() ? " (or press Next tip)" : " (unlock when you reach this step)");
			y = wrapped(g, text, rightX, y, rightW, 0xFF808080) + 3;
		}

		if (step.bestY() != null)
			y = wrapped(g, "Best height: Y " + step.bestY(), rightX, y + 3, rightW, TrackerOverlay.AQUA) + 3;
		if (step.locate() != null) {
			int until = ClientState.ticksUntilPointer(step);
			String text;
			if (ClientState.pointer() != null && step == ClientState.currentStep())
				text = "Pointer unlocked: follow the arrow on the tracker and the gold beam.";
			else if (until > 0)
				text = "Stuck? A pointer to the nearest " + step.locate().name() + " unlocks in " + TrackerOverlay.time(until) + ".";
			else
				text = "If you get stuck on this step, a pointer to the nearest " + step.locate().name() + " unlocks.";
			y = wrapped(g, text, rightX, y + 3, rightW, TrackerOverlay.AQUA) + 3;
		}
		if (step.blueprint() != null) {
			Blueprint bp = Blueprints.get(step.blueprint());
			if (bp != null)
				y = wrapped(g, "Blueprint: " + bp.name + " (press Build plan)", rightX, y + 3, rightW, TrackerOverlay.AQUA) + 3;
		}

		detailContentH = y - top;
		g.disableScissor();
	}

	private void drawBlueprintList(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		JourneySyncMessage.Build active = ClientState.build();
		g.enableScissor(midX, bodyY, midX + MID_W, bodyY + bodyH);
		int y = bodyY - listScroll;
		for (Blueprint bp : Blueprints.all()) {
			boolean selected = bp.id.equals(selectedBlueprint);
			boolean hover = inside(mouseX, mouseY, midX, y, MID_W, ROW_H - 1) && mouseY >= bodyY && mouseY < bodyY + bodyH;
			g.fill(midX, y, midX + MID_W, y + ROW_H - 1, selected ? 0x806B5A2E : hover ? 0x40FFFFFF : 0x18FFFFFF);
			g.item(bp.icon.getDefaultInstance(), midX + 2, y + 1);
			g.text(font, fit(bp.name, MID_W - 34), midX + 21, y + 6, TrackerOverlay.WHITE, false);
			boolean building = active != null && active.blueprintId().equals(bp.id);
			String mark = building ? "▶" : ClientState.isBuildComplete(bp.id) ? "✔" : "";
			g.text(font, mark, midX + MID_W - 10, y + 6, building ? TrackerOverlay.GOLD : TrackerOverlay.GREEN, false);
			y += ROW_H;
		}
		g.disableScissor();
	}

	private void drawBlueprintDetails(GuiGraphicsExtractor g) {
		Blueprint bp = Blueprints.get(selectedBlueprint);
		if (bp == null || minecraft.player == null)
			return;
		int bottom = py + ph - GAP - 24;
		g.enableScissor(rightX, bodyY, rightX + rightW, bottom);
		int y = bodyY - detailScroll;
		int top = y;

		g.item(bp.icon.getDefaultInstance(), rightX, y);
		g.text(font, bp.name, rightX + 20, y, TrackerOverlay.WHITE, true);
		JourneySyncMessage.Build active = ClientState.build();
		String status = active != null && active.blueprintId().equals(bp.id) ? "Building now"
				: ClientState.isBuildComplete(bp.id) ? "Built ✔" : "Not built yet";
		g.text(font, status, rightX + 20, y + 9, ClientState.isBuildComplete(bp.id) ? TrackerOverlay.GREEN : TrackerOverlay.GOLD, false);
		y += 22;

		y = wrapped(g, bp.description, rightX, y, rightW, TrackerOverlay.GRAY) + 4;
		y = wrapped(g, bp.width + " wide, " + bp.depth + " deep, " + bp.height + " layers, " + bp.blockCount() + " blocks",
				rightX, y, rightW, TrackerOverlay.AQUA) + 4;

		for (Step step : Journey.steps())
			if (bp.id.equals(step.blueprint())) {
				y = wrapped(g, "Part of step: " + step.title(), rightX, y, rightW, TrackerOverlay.AQUA) + 4;
				break;
			}

		g.text(font, "Materials (you have / need)", rightX, y, TrackerOverlay.GOLD, false);
		y += 12;
		for (Map.Entry<Block, Integer> e : bp.materials().entrySet()) {
			Block block = e.getKey();
			g.item(BuildCheck.itemFor(block).getDefaultInstance(), rightX, y);
			g.text(font, fit(block.getName().getString(), rightW - 70), rightX + 20, y + 4, TrackerOverlay.WHITE, false);
			String count;
			int color;
			if (BuildCheck.countsInventory(block)) {
				int have = BuildView.carried(minecraft.player, block);
				count = have + " / " + e.getValue();
				color = have >= e.getValue() ? TrackerOverlay.GREEN : TrackerOverlay.RED;
			} else {
				count = "x" + e.getValue();
				color = TrackerOverlay.GRAY;
			}
			g.text(font, count, rightX + rightW - font.width(count) - 2, y + 4, color, false);
			y += 18;
		}
		y += 2;
		y = wrapped(g, "Tip: build from the bottom layer up. Hold the right block and right-click a see-through block "
				+ "to place it there, already facing the right way.", rightX, y, rightW, 0xFF808080);

		detailContentH = y - top;
		g.disableScissor();
	}

	private int wrapped(GuiGraphicsExtractor g, String text, int x, int y, int width, int color) {
		for (FormattedCharSequence line : font.split(Component.literal(text), width)) {
			g.text(font, line, x, y, color, false);
			y += 9;
		}
		return y;
	}

	private String fit(String text, int width) {
		if (font.width(text) <= width)
			return text;
		return font.plainSubstrByWidth(text, width - font.width("...")) + "...";
	}

	private static boolean inside(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick))
			return true;
		double mx = event.x();
		double my = event.y();

		int y = bodyY;
		for (int i = 0; i <= BUILDS_TAB; i++) {
			if (inside(mx, my, leftX, y, LEFT_W, TAB_H - 2)) {
				if (tab != i) {
					tab = i;
					listScroll = 0;
					detailScroll = 0;
					click();
					rebuildWidgets();
				}
				return true;
			}
			y += TAB_H;
		}

		if (my >= bodyY && my < bodyY + bodyH && mx >= midX && mx < midX + MID_W) {
			int index = (int) ((my - bodyY + listScroll) / ROW_H);
			if (tab == BUILDS_TAB) {
				List<Blueprint> all = Blueprints.all();
				if (index >= 0 && index < all.size()) {
					selectedBlueprint = all.get(index).id;
					detailScroll = 0;
					click();
					rebuildWidgets();
					return true;
				}
			} else {
				List<Step> steps = Journey.inChapter(Chapter.values()[tab]);
				if (index >= 0 && index < steps.size()) {
					selectedStep = steps.get(index).id();
					detailScroll = 0;
					click();
					rebuildWidgets();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		int amount = (int) (-scrollY * 12);
		if (mx >= midX && mx < midX + MID_W) {
			int rows = tab == BUILDS_TAB ? Blueprints.all().size() : Journey.inChapter(Chapter.values()[tab]).size();
			listScroll = clamp(listScroll + amount, rows * ROW_H - bodyH);
			return true;
		}
		if (mx >= rightX && mx < rightX + rightW) {
			int visibleH = py + ph - GAP - 24 - bodyY;
			detailScroll = clamp(detailScroll + amount, detailContentH - visibleH + 4);
			return true;
		}
		return super.mouseScrolled(mx, my, scrollX, scrollY);
	}

	private static int clamp(int value, int max) {
		return Math.max(0, Math.min(value, Math.max(0, max)));
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (JourneyGuideModKeyMappings.OPEN_JOURNAL.matches(event)) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void tick() {
		if (ClientState.received() && stateChanged())
			rebuildWidgets();
	}

	private int lastStateHash;

	private boolean stateChanged() {
		Step step = selectedStep == null ? null : Journey.get(selectedStep);
		int hash = java.util.Objects.hash(tab, selectedStep, selectedBlueprint, ClientState.build(),
				step == null ? 0 : ClientState.isComplete(step), step == null ? 0 : ClientState.isSkipped(step),
				step == null ? 0 : ClientState.visibleTips(step), ClientState.currentStep());
		if (hash == lastStateHash)
			return false;
		lastStateHash = hash;
		return true;
	}

	private void click() {
		if (minecraft.player != null)
			minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.0F);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
