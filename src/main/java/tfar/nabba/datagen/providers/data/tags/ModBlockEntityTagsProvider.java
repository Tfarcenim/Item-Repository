package tfar.nabba.datagen.providers.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModBlockEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockEntityTagsProvider extends IntrinsicHolderTagsProvider<BlockEntityType<?>> {

    public ModBlockEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output,Registries.BLOCK_ENTITY_TYPE,
                lookupProvider,blockEntityType -> ForgeRegistries.BLOCK_ENTITY_TYPES.getResourceKey(blockEntityType).orElseThrow(), NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModBlockEntityTypeTags.BETTER_BARRELS).add(ModBlockEntityTypes.BETTER_BARREL,ModBlockEntityTypes.DISCRETE_BETTER_BARREL);
        this.tag(ModBlockEntityTypeTags.ANTI_BARRELS).add(ModBlockEntityTypes.ANTI_BARREL,ModBlockEntityTypes.DISCRETE_ANTI_BARREL);
        this.tag(ModBlockEntityTypeTags.FLUID_BARRELS).add(ModBlockEntityTypes.FLUID_BARREL,ModBlockEntityTypes.DISCRETE_FLUID_BARREL);
        this.tag(ModBlockEntityTypeTags.BARRELS).addTags(ModBlockEntityTypeTags.BETTER_BARRELS,ModBlockEntityTypeTags.ANTI_BARRELS,ModBlockEntityTypeTags.FLUID_BARRELS);
    }
}
