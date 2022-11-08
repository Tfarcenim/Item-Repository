package tfar.nabba.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL,ModBlocks.COPPER_BETTER_BARREL,
                ModBlocks.IRON_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,ModBlocks.GOLD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,
                ModBlocks.EMERALD_BETTER_BARREL,ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.CREATIVE_BETTER_BARREL);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ANTI_BARREL);
    }
}
