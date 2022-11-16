package tfar.nabba.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.Nonnull;

public class ShapelessSerializer2 extends ShapelessRecipe.Serializer {
    @Override
    public CopyNBTShapelessRecipe fromJson(ResourceLocation location, JsonObject json) {
        return new CopyNBTShapelessRecipe(super.fromJson(location, json));
    }


    @Override
    public CopyNBTShapelessRecipe fromNetwork(@Nonnull ResourceLocation p_199426_1_, FriendlyByteBuf p_199426_2_) {
        return new CopyNBTShapelessRecipe(super.fromNetwork(p_199426_1_, p_199426_2_));
    }
}
