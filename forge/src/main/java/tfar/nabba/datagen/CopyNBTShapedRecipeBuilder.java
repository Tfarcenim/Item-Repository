package tfar.nabba.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import tfar.nabba.init.ModRecipeSerializers;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CopyNBTShapedRecipeBuilder extends ShapedRecipeBuilder {
    public CopyNBTShapedRecipeBuilder(RecipeCategory category,ItemLike pResult, int pCount) {
        super(category,pResult, pCount);
    }

    public static ShapedRecipeBuilder shaped(RecipeCategory category,ItemLike pResult) {
        return shaped(category,pResult, 1);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeBuilder shaped(RecipeCategory category,ItemLike pResult, int pCount) {
        return new CopyNBTShapedRecipeBuilder(category,pResult, pCount);
    }


    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group,determineBookCategory(this.category), this.rows, this.key, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"),this.showNotification));
    }

    public static class Result extends ShapedRecipeBuilder.Result {


        public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, CraftingBookCategory pCategory, List<String> pPattern, Map<Character, Ingredient> pKey, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, boolean pShowNotification) {
            super(pId, pResult, pCount, pGroup, pCategory, pPattern, pKey, pAdvancement, pAdvancementId, pShowNotification);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.COPY_NBT_SHAPED;
        }
    }
}
