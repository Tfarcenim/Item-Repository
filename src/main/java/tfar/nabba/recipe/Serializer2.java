package tfar.nabba.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class Serializer2 extends ShapedRecipe.Serializer {
    @Override
    public CopyNBTShapedRecipe fromJson(ResourceLocation location, JsonObject json) {
        return new CopyNBTShapedRecipe(super.fromJson(location, json));
    }


    @Override
    public CopyNBTShapedRecipe fromNetwork(@Nonnull ResourceLocation p_199426_1_, FriendlyByteBuf p_199426_2_) {
        return new CopyNBTShapedRecipe(super.fromNetwork(p_199426_1_, p_199426_2_));
    }
}