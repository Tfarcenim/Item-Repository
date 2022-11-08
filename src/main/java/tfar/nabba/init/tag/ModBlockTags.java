package tfar.nabba.init.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import tfar.nabba.NABBA;

public class ModBlockTags {
    public static final TagKey<Block> BETTER_BARRELS = BlockTags.create(new ResourceLocation(NABBA.MODID,"better_barrels"));
    public static final TagKey<Block> ANTI_BARRELS = BlockTags.create(new ResourceLocation(NABBA.MODID,"anti_barrels"));

}
