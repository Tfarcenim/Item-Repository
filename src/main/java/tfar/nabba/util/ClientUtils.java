package tfar.nabba.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.client.FluidSpriteCache;

public class ClientUtils {

    public static void renderFluid(PoseStack matrices, int x, int y, FluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int color = renderProperties.getTintColor(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f,1);
        RenderSystem.enableDepthTest();
        GuiComponent.blit(matrices,x,y, 0, 16, 16, sprite);

        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x - .5, y + 11, 0);
        float scale = .75f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-1 * x, -1 * y, 0);
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().font.draw(matrices,abrev(fluidStack.getAmount()),x,y ,0xffffff);
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void renderFluid1(PoseStack matrices, int x, int y, FluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int color = renderProperties.getTintColor(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f,1);
        GuiComponent.blit(matrices,x,y, 0, 16, 16, sprite);

        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x - .5, y + 11, 700);
        float scale = .75f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-1 * x, -1 * y, 0);
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().font.draw(matrices,abrev(fluidStack.getAmount()),x,y ,0xffffff);
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static String abrev(int i) {
        return i+"";
    }

}
