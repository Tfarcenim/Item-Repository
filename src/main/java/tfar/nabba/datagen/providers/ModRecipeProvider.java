package tfar.nabba.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.tag.ModBlockTags;
import tfar.nabba.init.tag.ModItemTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModBlocks.BETTER_BARREL)
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .pattern("PSP")
                .pattern("PbP")
                .pattern("PSP")
                .unlockedBy("has_barrel", has(Blocks.BARREL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.ANTI_BARREL)
                .define('P', ModItemTags.NETHER_BRICKS)
                .define('S', ModItemTags.NETHER_BRICK_SLABS)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .pattern("PSP")
                .pattern("PbP")
                .pattern("PSP")
                .unlockedBy("has_barrel", has(Blocks.BARREL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.STORAGE_UPGRADE)
                .define('P',Tags.Items.RODS_WOODEN)
                .define('b', ModBlocks.BETTER_BARREL)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_better_barrel", has(ModBlocks.BETTER_BARREL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.VOID_UPGRADE)
                .define('P',Tags.Items.RODS_WOODEN)
                .define('b', Tags.Items.OBSIDIAN)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIAN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.CONTROLLER)
                .define('P',Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .define('H',Blocks.HOPPER)
                .define('b', Blocks.ENDER_CHEST)
                .pattern("PHP")
                .pattern("HbH")
                .pattern("PHP")
                .unlockedBy("has_ender_chest", has(Blocks.ENDER_CHEST))
                .save(consumer);

        betterBarrelFrameUpgrade(ModBlocks.STONE_BETTER_BARREL,ModBlocks.BETTER_BARREL,ItemTags.STONE_CRAFTING_MATERIALS,consumer);
        betterBarrelFrameUpgrade(ModBlocks.COPPER_BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL,Tags.Items.INGOTS_COPPER,consumer);
        betterBarrelFrameUpgrade(ModBlocks.IRON_BETTER_BARREL,ModBlocks.COPPER_BETTER_BARREL,Tags.Items.INGOTS_IRON,consumer);
        betterBarrelFrameUpgrade(ModBlocks.LAPIS_BETTER_BARREL,ModBlocks.IRON_BETTER_BARREL,Tags.Items.GEMS_LAPIS,consumer);
        betterBarrelFrameUpgrade(ModBlocks.GOLD_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,Tags.Items.INGOTS_GOLD,consumer);
        betterBarrelFrameUpgrade(ModBlocks.DIAMOND_BETTER_BARREL,ModBlocks.GOLD_BETTER_BARREL,Tags.Items.GEMS_DIAMOND,consumer);
        betterBarrelFrameUpgrade(ModBlocks.EMERALD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,Tags.Items.GEMS_EMERALD,consumer);
        betterBarrelFrameUpgrade(ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.EMERALD_BETTER_BARREL,Tags.Items.INGOTS_NETHERITE,consumer);

        antiBarrelFrameUpgrade(ModBlocks.STONE_ANTI_BARREL,ModBlocks.ANTI_BARREL,ItemTags.STONE_CRAFTING_MATERIALS,consumer);
        antiBarrelFrameUpgrade(ModBlocks.COPPER_ANTI_BARREL,ModBlocks.STONE_ANTI_BARREL,Tags.Items.INGOTS_COPPER,consumer);
        antiBarrelFrameUpgrade(ModBlocks.IRON_ANTI_BARREL,ModBlocks.COPPER_ANTI_BARREL,Tags.Items.INGOTS_IRON,consumer);

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


    protected static void betterBarrelFrameUpgrade(Block next, Block barrel, TagKey<Item> mats, Consumer<FinishedRecipe> consumer) {
        barrelFrameUpgrade(next, barrel, Ingredient.of(mats),Ingredient.of(Tags.Items.RODS_WOODEN), consumer);
    }

    protected static void antiBarrelFrameUpgrade(Block next, Block barrel, TagKey<Item> mats, Consumer<FinishedRecipe> consumer) {
        barrelFrameUpgrade(next, barrel, Ingredient.of(mats),Ingredient.of(Tags.Items.INGOTS_NETHER_BRICK), consumer);
    }

    protected static void barrelFrameUpgrade(Block next, Block barrel, Ingredient mats,Ingredient frame, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(next, 1)
                .define('P', mats)
                .define('S', frame)
                .define('b', barrel)
                .pattern("PSP")
                .pattern("SbS")
                .pattern("PSP")
                .unlockedBy("has_better_barrel", has(barrel))
                .save(consumer);
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
