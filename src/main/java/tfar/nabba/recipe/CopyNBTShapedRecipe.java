package tfar.nabba.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.nabba.init.ModRecipeSerializers;
import tfar.nabba.init.tag.ModItemTags;

import javax.annotation.Nonnull;

public class CopyNBTShapedRecipe extends ShapedRecipe {

    public CopyNBTShapedRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), "upgrade", recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem());
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack newBarrel = super.assemble(inv).copy();

        //search for barrel
        for (int i = 0; i < inv.getContainerSize();i++) {
            ItemStack oldBarrel = inv.getItem(i);
            if (oldBarrel.is(ModItemTags.BARRELS) && oldBarrel.hasTag()) {
                newBarrel.setTag(oldBarrel.getTag());
                return newBarrel;
            }
        }
        return newBarrel;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.COPY_NBT_SHAPED;
    }
}
