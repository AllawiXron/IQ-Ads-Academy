package xallawix.mod.journeyguide;

import xallawix.mod.journeyguide.build.HologramAnchor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, JourneyGuide.MODID);

    public static final RegistryObject<EntityType<HologramAnchor>> HOLOGRAM_ANCHOR = ENTITIES.register("hologram_anchor",
            () -> EntityType.Builder.<HologramAnchor>of(HologramAnchor::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .noSave()
                    .noSummon()
                    .clientTrackingRange(0)
                    .build(ENTITIES.key("hologram_anchor")));

    private ModEntities() {}

    public static void register(BusGroup modBus) {
        ENTITIES.register(modBus);
    }
}
