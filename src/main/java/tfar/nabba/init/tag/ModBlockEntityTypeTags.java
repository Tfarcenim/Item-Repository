package tfar.nabba.init.tag;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.nabba.NABBA;

public class ModBlockEntityTypeTags {
    public static final TagKey<BlockEntityType<?>> BARRELS = create("barrels");
    public static final TagKey<BlockEntityType<?>> BETTER_BARRELS = create("better_barrels");
    public static final TagKey<BlockEntityType<?>> ANTI_BARRELS = create("anti_barrels");
    public static final TagKey<BlockEntityType<?>> FLUID_BARRELS = create("fluid_barrels");

    public static TagKey<BlockEntityType<?>> create(String name) {
        return TagKey.create(Registry.BLOCK_ENTITY_TYPE_REGISTRY, new ResourceLocation(NABBA.MODID,name));
    }
}
