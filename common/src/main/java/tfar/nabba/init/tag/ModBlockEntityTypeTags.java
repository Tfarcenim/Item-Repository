package tfar.nabba.init.tag;

import tfar.nabba.NABBA;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityTypeTags {
    public static final TagKey<BlockEntityType<?>> BARRELS = create("barrels");
    public static final TagKey<BlockEntityType<?>> BETTER_BARRELS = create("better_barrels");
    public static final TagKey<BlockEntityType<?>> ANTI_BARRELS = create("anti_barrels");
    public static final TagKey<BlockEntityType<?>> FLUID_BARRELS = create("fluid_barrels");

    public static TagKey<BlockEntityType<?>> create(String name) {
        return TagKey.create(Registries.BLOCK_ENTITY_TYPE, new ResourceLocation(NABBA.MODID,name));
    }
}