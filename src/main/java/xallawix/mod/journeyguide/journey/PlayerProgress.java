package xallawix.mod.journeyguide.journey;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/** Everything the mod remembers about one player. Lives on the server and is saved with the player. */
public final class PlayerProgress {
    /** Bumped when step rules change, so old saves can be re-checked. */
    private static final int DATA_VERSION = 2;

    public final Set<String> completed = new LinkedHashSet<>();
    public final Set<String> skipped = new LinkedHashSet<>();
    public final Set<String> completedBuilds = new LinkedHashSet<>();

    /** The step the player is currently on, used to reset the "stuck" timer when it changes. */
    public String currentStep = "";
    public int ticksOnStep;
    /** Extra tips the player has unlocked for the current step (beyond the first one). */
    public int extraTips;
    public boolean welcomed;

    public @Nullable ActiveBuild activeBuild;

    // Runtime only, not saved.
    public @Nullable Pointer pointer;
    public boolean pointerSearched;
    public int lastSyncHash;
    public int ticksSinceSync;

    public record ActiveBuild(String blueprintId, BlockPos origin, Rotation rotation, Identifier dimension) {}

    public record Pointer(BlockPos pos, Identifier dimension, String name) {}

    public void resetStepTimer(String step) {
        currentStep = step;
        ticksOnStep = 0;
        extraTips = 0;
        pointer = null;
        pointerSearched = false;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("version", DATA_VERSION);
        tag.putString("completed", String.join(",", completed));
        tag.putString("skipped", String.join(",", skipped));
        tag.putString("builds", String.join(",", completedBuilds));
        tag.putString("current", currentStep);
        tag.putInt("ticksOnStep", ticksOnStep);
        tag.putInt("extraTips", extraTips);
        tag.putBoolean("welcomed", welcomed);
        if (activeBuild != null) {
            CompoundTag build = new CompoundTag();
            build.putString("id", activeBuild.blueprintId());
            build.putLong("origin", activeBuild.origin().asLong());
            build.putString("rotation", activeBuild.rotation().name());
            build.putString("dimension", activeBuild.dimension().toString());
            tag.put("activeBuild", build);
        }
        return tag;
    }

    public static PlayerProgress load(CompoundTag tag) {
        PlayerProgress p = new PlayerProgress();
        split(tag.getStringOr("completed", ""), p.completed);
        split(tag.getStringOr("skipped", ""), p.skipped);
        split(tag.getStringOr("builds", ""), p.completedBuilds);
        if (tag.getIntOr("version", 1) < 2) {
            // 0.2.0: "Cook a meal" no longer counts food found in chests and "Get iron" now needs 8 ingots,
            // so check those two again (unless the player skipped them on purpose)
            for (String changed : new String[]{"cooked_food", "iron"})
                if (!p.skipped.contains(changed))
                    p.completed.remove(changed);
        }
        p.currentStep = tag.getStringOr("current", "");
        p.ticksOnStep = tag.getIntOr("ticksOnStep", 0);
        p.extraTips = tag.getIntOr("extraTips", 0);
        p.welcomed = tag.getBooleanOr("welcomed", false);
        CompoundTag build = tag.getCompoundOrEmpty("activeBuild");
        String id = build.getStringOr("id", "");
        if (!id.isEmpty()) {
            Rotation rotation;
            try {
                rotation = Rotation.valueOf(build.getStringOr("rotation", "NONE"));
            } catch (IllegalArgumentException e) {
                rotation = Rotation.NONE;
            }
            Identifier dim = Identifier.tryParse(build.getStringOr("dimension", "minecraft:overworld"));
            p.activeBuild = new ActiveBuild(id, BlockPos.of(build.getLongOr("origin", 0L)), rotation,
                    dim == null ? Identifier.withDefaultNamespace("overworld") : dim);
        }
        return p;
    }

    private static void split(String joined, Set<String> into) {
        for (String s : joined.split(","))
            if (!s.isBlank())
                into.add(s.trim());
    }
}
