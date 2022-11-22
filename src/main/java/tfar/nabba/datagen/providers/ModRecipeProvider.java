package tfar.nabba.datagen.providers;

import com.mojang.datafixers.util.Pair;
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
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.datagen.CopyNBTShapedRecipeBuilder;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.tag.ModItemTags;
import tfar.nabba.item.BarrelFrameUpgradeItem;

import java.util.*;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    public static final List<BarrelFrameUpgradeItem> upgradeMap =  new ArrayList<>();

    static {
        for (Item item : ModItems.getItems()) {
            if (item instanceof BarrelFrameUpgradeItem barrelFrameUpgradeItem) {
                upgradeMap.add(barrelFrameUpgradeItem);
            }
        }
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

        ShapedRecipeBuilder.shaped(ModBlocks.FLUID_BARREL)
                .define('P', Tags.Items.INGOTS_COPPER)
                .define('S', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .pattern("PSP")
                .pattern("PbP")
                .pattern("PSP")
                .unlockedBy("has_barrel", has(Blocks.BARREL))
                .save(consumer);


        ShapedRecipeBuilder.shaped(ModItems.CONTROLLER)
                .define('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .define('H', Blocks.HOPPER)
                .define('b', Blocks.ENDER_CHEST)
                .pattern("PHP")
                .pattern("HbH")
                .pattern("PHP")
                .unlockedBy("has_ender_chest", has(Blocks.ENDER_CHEST))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.BARREL_INTERFACE)
                .define('P', Blocks.DEEPSLATE)
                .define('H', ModBlocks.DIAMOND_BETTER_BARREL)
                .define('b', ModBlocks.CONTROLLER)
                .pattern("PHP")
                .pattern("PbP")
                .pattern("PPP")
                .unlockedBy("has_controller", has(ModBlocks.CONTROLLER))
                .save(consumer);

        barrelFrames(consumer);
        itemUpgrades(consumer);
        compressionRecipes(consumer);
        barrelFrameUpgrades(consumer);
        keys(consumer);
    }

    protected void keys(Consumer<FinishedRecipe> consumer) {

    }

    protected void barrelFrames(Consumer<FinishedRecipe> consumer) {
        betterBarrelFrameUpgrade(ModBlocks.STONE_BETTER_BARREL, ModBlocks.BETTER_BARREL, ItemTags.STONE_CRAFTING_MATERIALS, consumer);
        betterBarrelFrameUpgrade(ModBlocks.COPPER_BETTER_BARREL, ModBlocks.STONE_BETTER_BARREL, Tags.Items.INGOTS_COPPER, consumer);
        betterBarrelFrameUpgrade(ModBlocks.IRON_BETTER_BARREL, ModBlocks.COPPER_BETTER_BARREL, Tags.Items.INGOTS_IRON, consumer);
        betterBarrelFrameUpgrade(ModBlocks.LAPIS_BETTER_BARREL, ModBlocks.IRON_BETTER_BARREL, Tags.Items.GEMS_LAPIS, consumer);
        betterBarrelFrameUpgrade(ModBlocks.GOLD_BETTER_BARREL, ModBlocks.LAPIS_BETTER_BARREL, Tags.Items.INGOTS_GOLD, consumer);
        betterBarrelFrameUpgrade(ModBlocks.DIAMOND_BETTER_BARREL, ModBlocks.GOLD_BETTER_BARREL, Tags.Items.GEMS_DIAMOND, consumer);
        betterBarrelFrameUpgrade(ModBlocks.EMERALD_BETTER_BARREL, ModBlocks.DIAMOND_BETTER_BARREL, Tags.Items.GEMS_EMERALD, consumer);
        betterBarrelFrameUpgrade(ModBlocks.NETHERITE_BETTER_BARREL, ModBlocks.EMERALD_BETTER_BARREL, Tags.Items.INGOTS_NETHERITE, consumer);

        betterBarrelFrameUpgrade(ModBlocks.STONE_ANTI_BARREL, ModBlocks.ANTI_BARREL, ItemTags.STONE_CRAFTING_MATERIALS, consumer);
        betterBarrelFrameUpgrade(ModBlocks.COPPER_ANTI_BARREL, ModBlocks.STONE_ANTI_BARREL, Tags.Items.INGOTS_COPPER, consumer);
        betterBarrelFrameUpgrade(ModBlocks.IRON_ANTI_BARREL, ModBlocks.COPPER_ANTI_BARREL, Tags.Items.INGOTS_IRON, consumer);
        betterBarrelFrameUpgrade(ModBlocks.LAPIS_ANTI_BARREL, ModBlocks.IRON_ANTI_BARREL, Tags.Items.GEMS_LAPIS, consumer);
        betterBarrelFrameUpgrade(ModBlocks.GOLD_ANTI_BARREL, ModBlocks.LAPIS_ANTI_BARREL, Tags.Items.INGOTS_GOLD, consumer);
        betterBarrelFrameUpgrade(ModBlocks.DIAMOND_ANTI_BARREL, ModBlocks.GOLD_ANTI_BARREL, Tags.Items.GEMS_DIAMOND, consumer);
        betterBarrelFrameUpgrade(ModBlocks.EMERALD_ANTI_BARREL, ModBlocks.DIAMOND_ANTI_BARREL, Tags.Items.GEMS_EMERALD, consumer);
        betterBarrelFrameUpgrade(ModBlocks.NETHERITE_ANTI_BARREL, ModBlocks.EMERALD_ANTI_BARREL, Tags.Items.INGOTS_NETHERITE, consumer);

        betterBarrelFrameUpgrade(ModBlocks.STONE_FLUID_BARREL, ModBlocks.FLUID_BARREL, ItemTags.STONE_CRAFTING_MATERIALS, consumer);
        betterBarrelFrameUpgrade(ModBlocks.COPPER_FLUID_BARREL, ModBlocks.STONE_FLUID_BARREL, Tags.Items.INGOTS_COPPER, consumer);
        betterBarrelFrameUpgrade(ModBlocks.IRON_FLUID_BARREL, ModBlocks.COPPER_FLUID_BARREL, Tags.Items.INGOTS_IRON, consumer);
        betterBarrelFrameUpgrade(ModBlocks.LAPIS_FLUID_BARREL, ModBlocks.IRON_FLUID_BARREL, Tags.Items.GEMS_LAPIS, consumer);
        betterBarrelFrameUpgrade(ModBlocks.GOLD_FLUID_BARREL, ModBlocks.LAPIS_FLUID_BARREL, Tags.Items.INGOTS_GOLD, consumer);
        betterBarrelFrameUpgrade(ModBlocks.DIAMOND_FLUID_BARREL, ModBlocks.GOLD_FLUID_BARREL, Tags.Items.GEMS_DIAMOND, consumer);
        betterBarrelFrameUpgrade(ModBlocks.EMERALD_FLUID_BARREL, ModBlocks.DIAMOND_FLUID_BARREL, Tags.Items.GEMS_EMERALD, consumer);
        betterBarrelFrameUpgrade(ModBlocks.NETHERITE_FLUID_BARREL, ModBlocks.EMERALD_FLUID_BARREL, Tags.Items.INGOTS_NETHERITE, consumer);
    }

    protected void barrelFrameUpgrades(Consumer<FinishedRecipe> consumer) {
        betterBarrelFrameUpgradeItem(ModItems.WOOD_TO_STONE_FRAME_UPGRADE,ItemTags.STONE_CRAFTING_MATERIALS, consumer);
        betterBarrelFrameUpgradeItem(ModItems.STONE_TO_COPPER_FRAME_UPGRADE, Tags.Items.INGOTS_COPPER, consumer);
        betterBarrelFrameUpgradeItem(ModItems.COPPER_TO_IRON_FRAME_UPGRADE, Tags.Items.INGOTS_IRON, consumer);
        betterBarrelFrameUpgradeItem(ModItems.IRON_TO_LAPIS_FRAME_UPGRADE,  Tags.Items.GEMS_LAPIS, consumer);
        betterBarrelFrameUpgradeItem(ModItems.LAPIS_TO_GOLD_FRAME_UPGRADE, Tags.Items.INGOTS_GOLD, consumer);
        betterBarrelFrameUpgradeItem(ModItems.GOLD_TO_DIAMOND_FRAME_UPGRADE,  Tags.Items.GEMS_DIAMOND, consumer);
        betterBarrelFrameUpgradeItem(ModItems.DIAMOND_TO_EMERALD_FRAME_UPGRADE,  Tags.Items.GEMS_EMERALD, consumer);
        betterBarrelFrameUpgradeItem(ModItems.EMERALD_TO_NETHERITE_FRAME_UPGRADE,  Tags.Items.INGOTS_NETHERITE, consumer);

        for (int i = 0; i < upgradeMap.size(); i++) {
            BarrelFrameUpgradeItem barrelFrameUpgradeItem = upgradeMap.get(i);
            for (int j = i+1; j < upgradeMap.size();j++) {
                BarrelFrameUpgradeItem barrelFrameUpgradeItem1 = upgradeMap.get(j);

                Pair<Integer,Integer> pair1 = barrelFrameUpgradeItem.getUpgr();
                Pair<Integer,Integer> pair2 = barrelFrameUpgradeItem1.getUpgr();

                if (pair1.getSecond().equals(pair2.getFirst()) || pair1.getFirst().equals(pair2.getSecond())) {
                    int i1 = pair1.getFirst();
                    int i2 = pair1.getSecond();
                    int i3 = pair2.getFirst();
                    int i4 = pair2.getSecond();

                    List<Integer> ints = List.of(i1,i2,i3,i4);

                    int j1 = Collections.min(ints);
                    int j2 = Collections.max(ints);
                    Pair<Integer,Integer> pair = Pair.of(j1,j2);
                    BarrelFrameUpgradeItem barrelFrameUpgradeItem2 = lookupPair(pair);

                    if (barrelFrameUpgradeItem2!= null) {
                        int variant = variants.getOrDefault(barrelFrameUpgradeItem2,0);

                        ShapelessRecipeBuilder.shapeless(barrelFrameUpgradeItem2)
                                .requires(barrelFrameUpgradeItem)
                                .requires(barrelFrameUpgradeItem1)
                                .unlockedBy("has_better_barrel", has(ModBlocks.BETTER_BARREL))
                                .save(consumer, new ResourceLocation(NABBA.MODID, RecipeBuilder.getDefaultRecipeId(barrelFrameUpgradeItem2).getPath() +
                                        "_variant_"+variant));
                        variants.put(barrelFrameUpgradeItem2,++variant);
                    }
                }
            }

            for (Block block : ModBlocks.getBlocks()) {
                if (block instanceof AbstractBarrelBlock abstractBarrelBlock) {
                    if (barrelFrameUpgradeItem.canUpgrade(abstractBarrelBlock)) {
                        AbstractBarrelBlock upBlock = barrelFrameUpgradeItem.getTo().getBarrel(abstractBarrelBlock.getType());
                        int variant = variants1.getOrDefault(upBlock,0);


                        ShapelessRecipeBuilder.shapeless(upBlock)
                                .requires(barrelFrameUpgradeItem)
                                .requires(abstractBarrelBlock)
                                .unlockedBy("has_better_barrel", has(ModBlocks.BETTER_BARREL))
                                .save(consumer, new ResourceLocation(NABBA.MODID, RecipeBuilder.getDefaultRecipeId(upBlock).getPath() +
                                        "_variant_"+variant));

                        variants1.put(upBlock,++variant);
                    }
                }
            }
        }
    }


    private BarrelFrameUpgradeItem lookupPair(Pair<Integer,Integer> pair) {
        for (BarrelFrameUpgradeItem barrelFrameUpgradeItem : upgradeMap) {
            if (barrelFrameUpgradeItem.getUpgr().equals(pair)) {
                return barrelFrameUpgradeItem;
            }
        }
        return null;
    }

    private static Map<BarrelFrameUpgradeItem,Integer> variants = new HashMap<>();

    private static Map<AbstractBarrelBlock,Integer> variants1 = new HashMap<>();



    protected void itemUpgrades(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModItems.BETTER_BARREL_STORAGE_UPGRADE)
                .define('P', Tags.Items.RODS_WOODEN)
                .define('b', ModBlocks.BETTER_BARREL)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_better_barrel", has(ModBlocks.BETTER_BARREL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.ANTI_BARREL_STORAGE_UPGRADE)
                .define('P', Items.NETHER_BRICK)
                .define('b', ModBlocks.ANTI_BARREL)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_anti_barrel", has(ModBlocks.ANTI_BARREL))
                .save(consumer);


        ShapedRecipeBuilder.shaped(ModItems.FLUID_BARREL_STORAGE_UPGRADE)
                .define('P', Tags.Items.NUGGETS_IRON)
                .define('b', ModBlocks.FLUID_BARREL)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_fluid_barrel", has(ModBlocks.FLUID_BARREL))
                .save(consumer);


        ShapedRecipeBuilder.shaped(ModItems.VOID_UPGRADE)
                .define('P', Tags.Items.RODS_WOODEN)
                .define('b', Tags.Items.OBSIDIAN)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIAN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.PICKUP_1x1_UPGRADE)
                .define('P', Tags.Items.RODS_WOODEN)
                .define('b', Blocks.HOPPER)
                .pattern("P P")
                .pattern(" b ")
                .pattern("P P")
                .unlockedBy("has_hopper", has(Blocks.HOPPER))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ModItems.PICKUP_3x3_UPGRADE)
                .requires(ModItems.PICKUP_1x1_UPGRADE, 3)
                .unlockedBy("has_upgrade", has(ModItems.PICKUP_1x1_UPGRADE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ModItems.PICKUP_9x9_UPGRADE)
                .requires(ModItems.PICKUP_3x3_UPGRADE, 3)
                .unlockedBy("has_upgrade", has(ModItems.PICKUP_3x3_UPGRADE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ModItems.PICKUP_1x1_UPGRADE, 3)
                .requires(ModItems.PICKUP_3x3_UPGRADE)
                .unlockedBy("has_upgrade", has(ModItems.PICKUP_3x3_UPGRADE))
                .save(consumer, new ResourceLocation(NABBA.MODID, RecipeBuilder.getDefaultRecipeId(ModItems.PICKUP_1x1_UPGRADE).getPath() + "_reverse"));

        ShapelessRecipeBuilder.shapeless(ModItems.PICKUP_3x3_UPGRADE, 3)
                .requires(ModItems.PICKUP_9x9_UPGRADE)
                .unlockedBy("has_upgrade", has(ModItems.PICKUP_9x9_UPGRADE))
                .save(consumer, new ResourceLocation(NABBA.MODID, RecipeBuilder.getDefaultRecipeId(ModItems.PICKUP_3x3_UPGRADE).getPath() + "_reverse"));

    }

    protected void compressionRecipes(Consumer<FinishedRecipe> consumer) {
        make2x2AndReverse(ModItems.x4_BETTER_BARREL_STORAGE_UPGRADE, ModItems.BETTER_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x16_BETTER_BARREL_STORAGE_UPGRADE, ModItems.x4_BETTER_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x64_BETTER_BARREL_STORAGE_UPGRADE, ModItems.x16_BETTER_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x256_BETTER_BARREL_STORAGE_UPGRADE, ModItems.x64_BETTER_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x1024_BETTER_BARREL_STORAGE_UPGRADE, ModItems.x256_BETTER_BARREL_STORAGE_UPGRADE, consumer);

        make2x2AndReverse(ModItems.x4_ANTI_BARREL_STORAGE_UPGRADE, ModItems.ANTI_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x16_ANTI_BARREL_STORAGE_UPGRADE, ModItems.x4_ANTI_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x64_ANTI_BARREL_STORAGE_UPGRADE, ModItems.x16_ANTI_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x256_ANTI_BARREL_STORAGE_UPGRADE, ModItems.x64_ANTI_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x1024_ANTI_BARREL_STORAGE_UPGRADE, ModItems.x256_ANTI_BARREL_STORAGE_UPGRADE, consumer);

        make2x2AndReverse(ModItems.x4_FLUID_BARREL_STORAGE_UPGRADE, ModItems.FLUID_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x16_FLUID_BARREL_STORAGE_UPGRADE, ModItems.x4_FLUID_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x64_FLUID_BARREL_STORAGE_UPGRADE, ModItems.x16_FLUID_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x256_FLUID_BARREL_STORAGE_UPGRADE, ModItems.x64_FLUID_BARREL_STORAGE_UPGRADE, consumer);
        make2x2AndReverse(ModItems.x1024_FLUID_BARREL_STORAGE_UPGRADE, ModItems.x256_FLUID_BARREL_STORAGE_UPGRADE, consumer);
    }


    protected static void make2x2AndReverse(Item compact, Item item, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(compact).define('#', item)
                .pattern("##").pattern("##").unlockedBy("has_storage_upgrade", has(item)).save(consumer);

        ShapelessRecipeBuilder.shapeless(item, 4).requires(compact)
                .unlockedBy("has_storage_upgrade", has(compact))
                .save(consumer, new ResourceLocation(NABBA.MODID, RecipeBuilder.getDefaultRecipeId(item).getPath() + "_reverse"));
    }


    protected static void betterBarrelFrameUpgrade(Block next, Block barrel, TagKey<Item> mats, Consumer<FinishedRecipe> consumer) {
        barrelFrameUpgrade(next, barrel, Ingredient.of(mats), Ingredient.of(Tags.Items.RODS_WOODEN), consumer);
    }

    protected static void betterBarrelFrameUpgradeItem(Item next, TagKey<Item> mats, Consumer<FinishedRecipe> consumer) {
        barrelFrameUpgradeItem(next, Ingredient.of(mats), Ingredient.of(Tags.Items.RODS_WOODEN), consumer);
    }

    protected static void antiBarrelFrameUpgrade(Block next, Block barrel, TagKey<Item> mats, Consumer<FinishedRecipe> consumer) {
        barrelFrameUpgrade(next, barrel, Ingredient.of(mats), Ingredient.of(Tags.Items.INGOTS_NETHER_BRICK), consumer);
    }

    protected static void barrelFrameUpgradeItem(Item next, Ingredient mats, Ingredient frame, Consumer<FinishedRecipe> consumer) {
        CopyNBTShapedRecipeBuilder.shaped(next, 1)
                .define('P', mats)
                .define('S', frame)
                .pattern("PSP")
                .pattern("S S")
                .pattern("PSP")
                .unlockedBy("has_better_barrel", has(ModBlocks.BETTER_BARREL))
                .save(consumer);
    }

    protected static void barrelFrameUpgrade(Block next, Block barrel, Ingredient mats, Ingredient frame, Consumer<FinishedRecipe> consumer) {
        CopyNBTShapedRecipeBuilder.shaped(next, 1)
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
        CopyNBTShapedRecipeBuilder.shaped(next, 1)
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
