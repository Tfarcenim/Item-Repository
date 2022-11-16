package tfar.nabba.datagen.providers.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModBlockEntityTypeTags;

public class ModBlockEntityTagsProvider extends TagsProvider<BlockEntityType<?>> {

    public ModBlockEntityTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registry.BLOCK_ENTITY_TYPE, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(ModBlockEntityTypeTags.BETTER_BARRELS).add(ModBlockEntityTypes.BETTER_BARREL,ModBlockEntityTypes.DISCRETE_BETTER_BARREL);
        this.tag(ModBlockEntityTypeTags.ANTI_BARRELS).add(ModBlockEntityTypes.ANTI_BARREL,ModBlockEntityTypes.DISCRETE_ANTI_BARREL);
        this.tag(ModBlockEntityTypeTags.FLUID_BARRELS).add(ModBlockEntityTypes.FLUID_BARREL,ModBlockEntityTypes.DISCRETE_FLUID_BARREL);
        this.tag(ModBlockEntityTypeTags.BARRELS).addTags(ModBlockEntityTypeTags.BETTER_BARRELS,ModBlockEntityTypeTags.ANTI_BARRELS,ModBlockEntityTypeTags.FLUID_BARRELS);
    }
}
