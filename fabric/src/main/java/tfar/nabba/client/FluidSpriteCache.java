package tfar.nabba.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import tfar.nabba.util.FabricFluidStack;

public class FluidSpriteCache {
    private static final LoadingCache<ResourceLocation, TextureAtlasSprite> SPRITE_CACHE = buildCache();

    public static TextureAtlasSprite getStillTexture(FabricFluidStack fluid) {
        return FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluidVariant().getFluid())
                .getFluidSprites(null,null,null)[0];
    }

    public static TextureAtlasSprite getFlowingTexture(FabricFluidStack fluid) {
        return FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluidVariant().getFluid())
                .getFluidSprites(null,null,null)[1];
    }

    public static void invalidateSpriteCache() {
        SPRITE_CACHE.invalidateAll();
    }

    private static LoadingCache<ResourceLocation, TextureAtlasSprite> buildCache() {
        //noinspection deprecation
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(CacheLoader.from(key -> Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(key)));
    }
}
