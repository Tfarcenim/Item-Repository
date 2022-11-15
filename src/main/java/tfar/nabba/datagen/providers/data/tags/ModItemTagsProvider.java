package tfar.nabba.datagen.providers.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.tag.ModBlockTags;
import tfar.nabba.init.tag.ModItemTags;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        copy(ModBlockTags.BETTER_BARRELS, ModItemTags.BETTER_BARRELS);
        copy(ModBlockTags.ANTI_BARRELS, ModItemTags.ANTI_BARRELS);
        copy(ModBlockTags.NETHER_BRICKS,ModItemTags.NETHER_BRICKS);
        copy(ModBlockTags.NETHER_BRICK_SLABS,ModItemTags.NETHER_BRICK_SLABS);
        tag(ModItemTags.KEYS).add(ModItems.HIDE_KEY,ModItems.LOCK_KEY,ModItems.VANITY_KEY,ModItems.CONTROLLER_KEY);
    }
}
