package tfar.nabba.init.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import tfar.nabba.NABBA;

public class ModBlockTags {
    public static final TagKey<Block> BARRELS = BlockTags.create(new ResourceLocation(NABBA.MODID,"barrels"));
    public static final TagKey<Block> BETTER_BARRELS = BlockTags.create(new ResourceLocation(NABBA.MODID,"better_barrels"));
    public static final TagKey<Block> ANTI_BARRELS = BlockTags.create(new ResourceLocation(NABBA.MODID,"anti_barrels"));
    public static final TagKey<Block> FLUID_BARRELS = BlockTags.create(new ResourceLocation(NABBA.MODID,"fluid_barrels"));
    public static final TagKey<Block> NETHER_BRICKS = BlockTags.create(new ResourceLocation("forge","blocks/nether_bricks"));
    public static final TagKey<Block> NETHER_BRICK_SLABS = BlockTags.create(new ResourceLocation("forge","blocks/nether_brick_slabs"));

}
