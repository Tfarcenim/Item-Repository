package tfar.nabba.datagen.providers.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.tag.ModBlockTags;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(ModBlockTags.BETTER_BARRELS).add(ModBlocks.BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL,ModBlocks.COPPER_BETTER_BARREL,
                ModBlocks.IRON_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,ModBlocks.GOLD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,
                ModBlocks.EMERALD_BETTER_BARREL,ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.CREATIVE_BETTER_BARREL);

        this.tag(ModBlockTags.ANTI_BARRELS).add(ModBlocks.ANTI_BARREL);

        this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(ModBlockTags.BETTER_BARRELS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(ModBlockTags.ANTI_BARRELS);
        this.tag(ModBlockTags.NETHER_BRICKS).add(Blocks.NETHER_BRICKS,Blocks.RED_NETHER_BRICKS,Blocks.CHISELED_NETHER_BRICKS,Blocks.CRACKED_NETHER_BRICKS);
        this.tag(ModBlockTags.NETHER_BRICK_SLABS).add(Blocks.NETHER_BRICK_SLAB,Blocks.RED_NETHER_BRICK_SLAB);
    }
}
