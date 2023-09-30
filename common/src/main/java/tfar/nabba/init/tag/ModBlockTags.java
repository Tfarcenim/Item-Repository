package tfar.nabba.init.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import tfar.nabba.NABBA;

public class ModBlockTags {
    public static final TagKey<Block> BARRELS = create(new ResourceLocation(NABBA.MODID,"barrels"));
    public static final TagKey<Block> BETTER_BARRELS = create(new ResourceLocation(NABBA.MODID,"better_barrels"));
    public static final TagKey<Block> ANTI_BARRELS = create(new ResourceLocation(NABBA.MODID,"anti_barrels"));
    public static final TagKey<Block> FLUID_BARRELS = create(new ResourceLocation(NABBA.MODID,"fluid_barrels"));
    public static final TagKey<Block> NETHER_BRICKS = create(new ResourceLocation("forge","blocks/nether_bricks"));
    public static final TagKey<Block> NETHER_BRICK_SLABS = create(new ResourceLocation("forge","blocks/nether_brick_slabs"));

    private static TagKey<Block> create(ResourceLocation $$0) {
        return TagKey.create(Registries.BLOCK, $$0);
    }

}
