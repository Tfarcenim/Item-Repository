package tfar.nabba.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.client.FluidSpriteCache;

public class ClientUtils {

    public static void renderFluid(GuiGraphics matrices, int x, int y, FluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int color = renderProperties.getTintColor(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 0, 16, 16, sprite);

        drawSmallFluidNumbers(matrices, x, y, 0, fluidStack);

    }

    public static void renderFluidTooltip(GuiGraphics matrices, int x, int y, FluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int color = renderProperties.getTintColor(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);

        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 400, 16, 16, sprite);

        drawSmallFluidNumbers(matrices, x, y, 500, fluidStack);
    }

    public static void drawSmallFluidNumbers(GuiGraphics matrices, int x, int y, int z, FluidStack fluidStack) {
        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x + 16, y + 12, z);
        float scale = .5f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-1 * x, -1 * y, 0);
        RenderSystem.applyModelViewMatrix();
        String s = CommonUtils.formatLargeNumber(fluidStack.getAmount());
        matrices.drawString(Minecraft.getInstance().font, s, x - Minecraft.getInstance().font.width(s), y, 0xffffff);
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void drawSmallItemNumbers(GuiGraphics matrices, int x, int y, ItemStack stack) {


        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x + 8, y + 8, 100);
        float scale = .5f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-x, -y, 0);
        RenderSystem.applyModelViewMatrix();
        String amount = (stack.getCount() > 1) ? CommonUtils.formatLargeNumber(stack.getCount()) : null;
        matrices.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, amount);
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();

    }
}
