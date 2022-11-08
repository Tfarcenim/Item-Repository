package tfar.itemrepository.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import tfar.itemrepository.init.ModBlocks;
import tfar.itemrepository.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.BETTER_BARREL, 1)
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('b', Blocks.BARREL)
                .pattern("PSP")
                .pattern("PbP")
                .pattern("PSP")
                .unlockedBy("has_barrel", has(Blocks.BARREL))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(ModItems.STORAGE_UPGRADE, 1)
                .define('P', Tags.Items.RODS_WOODEN)
                .define('b', ModBlocks.BETTER_BARREL)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_better_barrel", has(ModBlocks.BETTER_BARREL))
                .save(pFinishedRecipeConsumer);
    }
}
