package tfar.nabba.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import tfar.nabba.client.FluidSpriteCache;
import tfar.nabba.client.StackSizeRenderer;

public class ClientUtils {

    public static void renderFluidinSlot(GuiGraphics matrices, int x, int y, FabricFluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidStack.getFluidVariant().getFluid());
        int color = fluidRenderHandler.getFluidColor(null,null,null);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1,1,1,1);

        String s = CommonUtils.formatLargeNumber(fluidStack.getAmount()/81);
        StackSizeRenderer.renderSizeLabel(matrices,Minecraft.getInstance().font,x,y,s);
    }

    public static void renderFluidTooltip(GuiGraphics matrices, int x, int y, FabricFluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidStack.getFluidVariant().getFluid());
        int color = fluidRenderHandler.getFluidColor(null,null,null);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);

        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 400, 16, 16, sprite);
        RenderSystem.setShaderColor(1,1,1,1);
        String s = CommonUtils.formatLargeNumber(fluidStack.getAmount()/81);
        StackSizeRenderer.renderSizeLabel(matrices,Minecraft.getInstance().font,x,y,s);
    }
}
