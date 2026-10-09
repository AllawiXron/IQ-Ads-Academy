package com.allawixron.journeyguide.client;

import com.allawixron.journeyguide.build.Blueprint;
import com.allawixron.journeyguide.build.Blueprints;
import com.allawixron.journeyguide.journey.Chapter;
import com.allawixron.journeyguide.journey.Journey;
import com.allawixron.journeyguide.journey.Step;
import com.allawixron.journeyguide.network.SyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** What the client knows about the player's journey, as last sent by the server. */
public final class ClientState {
    private static boolean received;
    private static Set<String> completed = Set.of();
    private static Set<String> skipped = Set.of();
    private static Set<String> completedBuilds = Set.of();
    private static Map<String, Integer> progress = Map.of();
    private static String currentStep = "";
    private static int ticksOnStep;
    private static int extraTips;
    private static int extraTipTicks = 6 * 60 * 20;
    private static int pointerTicks = 15 * 60 * 20;
    private static SyncPacket.@Nullable Pointer pointer;
    private static SyncPacket.@Nullable Build build;

    private ClientState() {}

    public static void apply(SyncPacket msg) {
        Set<String> newCompleted = new HashSet<>(msg.completed());
        Set<String> newBuilds = new HashSet<>(msg.completedBuilds());
        String oldCurrent = currentStep;
        Set<String> oldCompleted = completed;

        if (received) {
            List<Step> justDone = new ArrayList<>();
            for (Step step : Journey.steps())
                if (newCompleted.contains(step.id()) && !oldCompleted.contains(step.id()) && !msg.skipped().contains(step.id()))
                    justDone.add(step);
            // a few toasts at most, so finishing many steps at once doesn't flood the screen
            for (int i = 0; i < Math.min(justDone.size(), 3); i++)
                onStepCompleted(justDone.get(i), newCompleted, justDone.get(i).id().equals(oldCurrent));
            if (justDone.size() > 3)
                StepToast.show("More steps done", "+" + (justDone.size() - 3) + " more, see the journal", justDone.getLast().iconStack());

            for (String id : newBuilds) {
                if (!completedBuilds.contains(id)) {
                    Blueprint bp = Blueprints.get(id);
                    if (bp != null)
                        StepToast.show("Build complete!", bp.name, bp.icon.getDefaultInstance());
                }
            }

            if (!msg.currentStep().equals(oldCurrent))
                explainSkippedAhead(oldCurrent, msg.currentStep(), oldCompleted, newCompleted);
        }

        received = true;
        completed = newCompleted;
        skipped = new HashSet<>(msg.skipped());
        completedBuilds = newBuilds;
        progress = msg.progress();
        currentStep = msg.currentStep();
        ticksOnStep = msg.ticksOnStep();
        extraTips = msg.extraTips();
        extraTipTicks = Math.max(20, msg.extraTipTicks());
        pointerTicks = msg.pointerTicks();
        pointer = msg.pointer();
        build = msg.build();
    }

    /**
     * When the tracker jumps over steps the player finished earlier, say so in chat. Otherwise it looks
     * like the guide skipped them by mistake.
     */
    private static void explainSkippedAhead(String oldCurrent, String newCurrent, Set<String> oldCompleted, Set<String> nowCompleted) {
        Step from = Journey.get(oldCurrent);
        Step to = Journey.get(newCurrent);
        if (from == null)
            return;
        int start = Journey.indexOf(from) + 1;
        int end = to == null ? Journey.steps().size() : Journey.indexOf(to);
        List<String> alreadyDone = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Step s = Journey.steps().get(i);
            if (oldCompleted.contains(s.id()) && nowCompleted.contains(s.id()))
                alreadyDone.add(s.title());
        }
        Minecraft mc = Minecraft.getInstance();
        if (alreadyDone.isEmpty() || mc.player == null)
            return;
        Component text = Component.literal("[Journey Guide] ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("You already did " + String.join(", ", alreadyDone) + " earlier, so "
                                + (alreadyDone.size() == 1 ? "it's" : "they're") + " ticked off. ")
                        .withStyle(ChatFormatting.GRAY))
                .append(Component.literal(to == null ? "That was the last step!" : "Next up: " + to.title())
                        .withStyle(ChatFormatting.YELLOW));
        mc.gui.hud.getChat().addClientSystemMessage(text);
    }

    private static void onStepCompleted(Step step, Set<String> nowCompleted, boolean wasCurrent) {
        boolean chapterDone = true;
        for (Step s : Journey.inChapter(step.chapter()))
            if (!nowCompleted.contains(s.id()))
                chapterDone = false;

        Minecraft mc = Minecraft.getInstance();
        if (step.id().equals("kill_dragon")) {
            mc.gui.hud.setTitle(Component.literal("You beat Minecraft!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            mc.gui.hud.setSubtitle(Component.literal("The journey is complete. What will you build next?").withStyle(ChatFormatting.YELLOW));
            if (mc.player != null)
                mc.player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
        } else if (chapterDone) {
            StepToast.show("Chapter complete!", step.chapter().title, step.chapter().icon());
            if (mc.player != null)
                mc.player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.7F, 1.0F);
        } else if (wasCurrent) {
            StepToast.show("Step complete!", step.title(), step.iconStack());
            if (mc.player != null)
                mc.player.playSound(SoundEvents.PLAYER_LEVELUP, 0.5F, 1.4F);
        } else {
            // finished a later step before getting there; it's ticked off now and won't be asked again
            StepToast.show("Done early!", step.title() + " (step " + (Journey.indexOf(step) + 1) + ")", step.iconStack());
        }
    }

    public static void reset() {
        received = false;
        completed = Set.of();
        skipped = Set.of();
        completedBuilds = Set.of();
        progress = Map.of();
        currentStep = "";
        pointer = null;
        build = null;
    }

    public static void tick() {
        if (received)
            ticksOnStep++;
    }

    // ------------------------------------------------------------------ queries

    public static boolean received() {
        return received;
    }

    public static @Nullable Step currentStep() {
        return currentStep.isEmpty() ? null : Journey.get(currentStep);
    }

    public static boolean isComplete(Step step) {
        return completed.contains(step.id());
    }

    public static boolean isSkipped(Step step) {
        return skipped.contains(step.id());
    }

    public static boolean isBuildComplete(String blueprintId) {
        return completedBuilds.contains(blueprintId);
    }

    public static int progress(Step step) {
        if (isComplete(step))
            return step.goal().target();
        return progress.getOrDefault(step.id(), 0);
    }

    public static int completedCount() {
        int n = 0;
        for (Step step : Journey.steps())
            if (isComplete(step))
                n++;
        return n;
    }

    public static int completedInChapter(Chapter chapter) {
        int n = 0;
        for (Step step : Journey.inChapter(chapter))
            if (isComplete(step))
                n++;
        return n;
    }

    /** How many tips of this step the player can read. */
    public static int visibleTips(Step step) {
        List<String> tips = step.tips();
        if (!step.id().equals(currentStep))
            return isComplete(step) ? tips.size() : Math.min(1, tips.size());
        int unlocked = 1 + extraTips + ticksOnStep / extraTipTicks;
        return Math.min(unlocked, tips.size());
    }

    /** Ticks until the next tip unlocks by itself, or -1 if all are visible. */
    public static int ticksUntilNextTip(Step step) {
        if (!step.id().equals(currentStep) || visibleTips(step) >= step.tips().size())
            return -1;
        return extraTipTicks - ticksOnStep % extraTipTicks;
    }

    /** Ticks until the pointer unlocks for the current step, 0 if unlocked, -1 if this step has none. */
    public static int ticksUntilPointer(Step step) {
        if (step.locate() == null || !step.id().equals(currentStep))
            return -1;
        return Math.max(0, pointerTicks - ticksOnStep);
    }

    public static SyncPacket.@Nullable Pointer pointer() {
        return pointer;
    }

    public static SyncPacket.@Nullable Build build() {
        return build;
    }
}
