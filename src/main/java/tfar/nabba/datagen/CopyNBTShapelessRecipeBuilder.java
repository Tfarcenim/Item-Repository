package tfar.nabba.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import tfar.nabba.init.ModRecipeSerializers;

import java.util.List;

public class CopyNBTShapelessRecipeBuilder extends ShapelessRecipeBuilder {
    public CopyNBTShapelessRecipeBuilder(ItemLike pResult, int pCount) {
        super(pResult, pCount);
    }

    public static ShapelessRecipeBuilder shapeless(ItemLike pResult) {
        return shapeless(pResult, 1);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static ShapelessRecipeBuilder shapeless(ItemLike pResult, int pCount) {
        return new CopyNBTShapelessRecipeBuilder(pResult, pCount);
    }

    public static class Result extends ShapelessRecipeBuilder.Result {

        public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, List<Ingredient> pIngredients, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            super(pId, pResult, pCount, pGroup, pIngredients, pAdvancement, pAdvancementId);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.COPY_NBT_SHAPELESS;
        }
    }
}
