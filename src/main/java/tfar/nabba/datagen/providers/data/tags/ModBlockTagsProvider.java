package tfar.nabba.datagen.providers.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.tag.ModBlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output,lookupProvider, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        this.tag(ModBlockTags.BETTER_BARRELS).add(ModBlocks.BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL,ModBlocks.COPPER_BETTER_BARREL,
                ModBlocks.IRON_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,ModBlocks.GOLD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,
                ModBlocks.EMERALD_BETTER_BARREL,ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.CREATIVE_BETTER_BARREL);

        this.tag(ModBlockTags.ANTI_BARRELS).add(ModBlocks.ANTI_BARREL,ModBlocks.STONE_ANTI_BARREL,ModBlocks.COPPER_ANTI_BARREL,
                ModBlocks.IRON_ANTI_BARREL,ModBlocks.LAPIS_ANTI_BARREL,ModBlocks.GOLD_ANTI_BARREL,ModBlocks.DIAMOND_ANTI_BARREL,
                ModBlocks.EMERALD_ANTI_BARREL,ModBlocks.NETHERITE_ANTI_BARREL,ModBlocks.CREATIVE_ANTI_BARREL);

        this.tag(ModBlockTags.FLUID_BARRELS).add(ModBlocks.FLUID_BARREL,ModBlocks.STONE_FLUID_BARREL,ModBlocks.COPPER_FLUID_BARREL,
                ModBlocks.IRON_FLUID_BARREL,ModBlocks.LAPIS_FLUID_BARREL,ModBlocks.GOLD_FLUID_BARREL,ModBlocks.DIAMOND_FLUID_BARREL,
                ModBlocks.EMERALD_FLUID_BARREL,ModBlocks.NETHERITE_FLUID_BARREL,ModBlocks.CREATIVE_FLUID_BARREL);

        this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(ModBlockTags.BETTER_BARRELS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).addTags(ModBlockTags.ANTI_BARRELS,ModBlockTags.FLUID_BARRELS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CONTROLLER,ModBlocks.CONTROLLER_PROXY,ModBlocks.BARREL_INTERFACE);
        this.tag(ModBlockTags.BARRELS).addTags(ModBlockTags.ANTI_BARRELS,ModBlockTags.BETTER_BARRELS,ModBlockTags.FLUID_BARRELS);
        
        this.tag(ModBlockTags.NETHER_BRICKS).add(Blocks.NETHER_BRICKS,Blocks.RED_NETHER_BRICKS,Blocks.CHISELED_NETHER_BRICKS,Blocks.CRACKED_NETHER_BRICKS);
        this.tag(ModBlockTags.NETHER_BRICK_SLABS).add(Blocks.NETHER_BRICK_SLAB,Blocks.RED_NETHER_BRICK_SLAB);
    }
}
