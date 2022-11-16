package tfar.nabba.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import tfar.nabba.init.ModRecipeSerializers;
import tfar.nabba.init.tag.ModItemTags;

import javax.annotation.Nonnull;

public class CopyNBTShapelessRecipe extends ShapelessRecipe {

    public CopyNBTShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), "upgrade", recipe.getResultItem(), recipe.getIngredients());
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
        return ModRecipeSerializers.COPY_NBT_SHAPELESS;
    }
}
