package xallawix.mod.journeyguide.build;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * An invisible entity that only ever exists on the client. Its renderer draws the hologram blocks, because
 * entity renderers are the place where the game lets mods draw textured, see-through geometry every frame.
 * It follows the player around so it is always in a visible chunk section. It never saves and the server
 * never spawns it.
 */
public final class HologramAnchor extends Entity {
    public HologramAnchor(EntityType<? extends HologramAnchor> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        // nothing to do: the client moves it next to the player every tick
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }
}
