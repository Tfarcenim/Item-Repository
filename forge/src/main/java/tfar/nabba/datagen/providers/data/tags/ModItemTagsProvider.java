package tfar.nabba.datagen.providers.data.tags;

import tfar.nabba.NABBA;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.tag.ModBlockTags;
import tfar.nabba.init.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, pBlockTagsProvider.contentsGetter(), NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        copy(ModBlockTags.BETTER_BARRELS, ModItemTags.BETTER_BARRELS);
        copy(ModBlockTags.ANTI_BARRELS, ModItemTags.ANTI_BARRELS);
        copy(ModBlockTags.FLUID_BARRELS,ModItemTags.FLUID_BARRELS);
        copy(ModBlockTags.BARRELS,ModItemTags.BARRELS);
        copy(ModBlockTags.NETHER_BRICKS,ModItemTags.NETHER_BRICKS);
        copy(ModBlockTags.NETHER_BRICK_SLABS,ModItemTags.NETHER_BRICK_SLABS);
        tag(ModItemTags.KEYS).add(ModItems.HIDE_KEY,ModItems.LOCK_KEY,ModItems.VANITY_KEY,ModItems.CONTROLLER_KEY,
                ModItems.FLUID_CONTROLLER_KEY,ModItems.CONNECT_KEY,ModItems.REMOTE_CONTROLLER_KEY,ModItems.REMOTE_FLUID_CONTROLLER_KEY);
    }
}
