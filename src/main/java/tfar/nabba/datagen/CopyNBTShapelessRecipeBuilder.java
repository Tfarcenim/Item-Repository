package tfar.nabba.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import tfar.nabba.init.ModRecipeSerializers;

import java.util.List;
import java.util.function.Consumer;

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

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + pRecipeId.getPath())));
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
