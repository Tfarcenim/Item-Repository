package tfar.nabba.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.BETTER_BARREL, 1)
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('b', Blocks.BARREL)
                .pattern("PSP")
                .pattern("PbP")
                .pattern("PSP")
                .unlockedBy("has_barrel", has(Blocks.BARREL))
                .save(consumer);

        barrelFrameUpgrade(ModBlocks.STONE_BETTER_BARREL,ModBlocks.BETTER_BARREL,ItemTags.STONE_CRAFTING_MATERIALS,consumer);
        barrelFrameUpgrade(ModBlocks.COPPER_BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL,Tags.Items.INGOTS_COPPER,consumer);
        barrelFrameUpgrade(ModBlocks.IRON_BETTER_BARREL,ModBlocks.COPPER_BETTER_BARREL,Tags.Items.INGOTS_IRON,consumer);
        barrelFrameUpgrade(ModBlocks.LAPIS_BETTER_BARREL,ModBlocks.IRON_BETTER_BARREL,Tags.Items.GEMS_LAPIS,consumer);
        barrelFrameUpgrade(ModBlocks.GOLD_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,Tags.Items.INGOTS_GOLD,consumer);
        barrelFrameUpgrade(ModBlocks.DIAMOND_BETTER_BARREL,ModBlocks.GOLD_BETTER_BARREL,Tags.Items.GEMS_DIAMOND,consumer);
        barrelFrameUpgrade(ModBlocks.EMERALD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,Tags.Items.GEMS_EMERALD,consumer);
        barrelFrameUpgrade(ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.EMERALD_BETTER_BARREL,Tags.Items.INGOTS_NETHERITE,consumer);

        make2x2AndReverse(ModItems.x4_STORAGE_UPGRADE,ModItems.STORAGE_UPGRADE,consumer);
        make2x2AndReverse(ModItems.x16_STORAGE_UPGRADE,ModItems.x4_STORAGE_UPGRADE,consumer);
        make2x2AndReverse(ModItems.x64_STORAGE_UPGRADE,ModItems.x16_STORAGE_UPGRADE,consumer);
        make2x2AndReverse(ModItems.x256_STORAGE_UPGRADE,ModItems.x64_STORAGE_UPGRADE,consumer);
        make2x2AndReverse(ModItems.x1024_STORAGE_UPGRADE,ModItems.x256_STORAGE_UPGRADE,consumer);
    }

    protected static void make2x2AndReverse(Item compact,Item item, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(compact).define('#', item)
                .pattern("##").pattern("##").unlockedBy("has_storage_upgrade", has(item)).save(consumer);

        ShapelessRecipeBuilder.shapeless(item,4).requires(compact)
                .unlockedBy("has_storage_upgrade", has(compact))
                .save(consumer,new ResourceLocation(NABBA.MODID,RecipeBuilder.getDefaultRecipeId(item).getPath()+"_reverse"));
    }


    protected static void barrelFrameUpgrade(Block next, Block barrel, TagKey<Item> mats, Consumer<FinishedRecipe> consumer) {
        barrelFrameUpgrade(next, barrel, Ingredient.of(mats), consumer);
    }
    protected static void barrelFrameUpgrade(Block next, Block barrel, Ingredient mats, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(next, 1)
                .define('P', mats)
                .define('S', Tags.Items.RODS_WOODEN)
                .define('b', barrel)
                .pattern("PSP")
                .pattern("SbS")
                .pattern("PSP")
                .unlockedBy("has_better_barrel", has(barrel))
                .save(consumer);
    }
}
