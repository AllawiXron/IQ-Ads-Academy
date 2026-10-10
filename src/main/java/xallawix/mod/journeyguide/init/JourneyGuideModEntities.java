package xallawix.mod.journeyguide.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xallawix.mod.journeyguide.JourneyGuideMod;
import xallawix.mod.journeyguide.entity.HologramAnchorEntity;

public class JourneyGuideModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, JourneyGuideMod.MODID);
	public static final RegistryObject<EntityType<HologramAnchorEntity>> HOLOGRAM_ANCHOR = REGISTRY.register("hologram_anchor",
			() -> EntityType.Builder.<HologramAnchorEntity>of(HologramAnchorEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).noSave().noSummon().clientTrackingRange(0)
					.build(REGISTRY.key("hologram_anchor")));
}
