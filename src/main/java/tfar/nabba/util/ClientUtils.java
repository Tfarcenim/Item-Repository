package tfar.nabba.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
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
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();

        GuiComponent.blit(matrices, x, y, 0, 16, 16, sprite);

        drawSmallFluidNumbers(matrices, x, y, 0, fluidStack);

    }

    public static void renderFluidTooltip(PoseStack matrices, int x, int y, FluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int color = renderProperties.getTintColor(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);

        RenderSystem.enableDepthTest();

        GuiComponent.blit(matrices, x, y, 400, 16, 16, sprite);

        drawSmallFluidNumbers(matrices, x, y, 500, fluidStack);
    }

    public static void drawSmallFluidNumbers(PoseStack matrices, int x, int y, int z, FluidStack fluidStack) {
        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x + 16, y + 12, z);
        float scale = .5f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-1 * x, -1 * y, 0);
        RenderSystem.applyModelViewMatrix();
        String s = Utils.formatLargeNumber(fluidStack.getAmount());

        Minecraft.getInstance().font.drawShadow(matrices, s, x - Minecraft.getInstance().font.width(s), y, 0xffffff);
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void drawSmallItemNumbers(PoseStack matrices, int x, int y, ItemStack stack) {


        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x + 8, y + 8, 100);
        float scale = .5f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-x, -y, 0);
        RenderSystem.applyModelViewMatrix();
        if (stack.getCount() > 1) {
            String amount = Utils.formatLargeNumber(stack.getCount());
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, x, y, amount);
        }
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();

    }


    public static void renderBox(Camera camera, BlockPos pos, int aarrggbb) {

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        RenderSystem.lineWidth(1.0F);
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        renderLine(camera, bufferbuilder, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ(), aarrggbb);
        renderLine(camera, bufferbuilder, pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ() + 1, aarrggbb);
        renderLine(camera, bufferbuilder, pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1, aarrggbb);
        renderLine(camera, bufferbuilder, pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1, aarrggbb);

        tesselator.end();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        renderLine(camera, bufferbuilder, pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ(), aarrggbb);
        renderLine(camera, bufferbuilder, pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ() + 1, aarrggbb);
        renderLine(camera, bufferbuilder, pos.getX(), pos.getY() + 1, pos.getZ() + 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, aarrggbb);
        renderLine(camera, bufferbuilder, pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, aarrggbb);
        tesselator.end();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        renderLine(camera, bufferbuilder, pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ(), aarrggbb);
        tesselator.end();

        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        renderLine(camera, bufferbuilder, pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ(), aarrggbb);
        tesselator.end();

        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        renderLine(camera, bufferbuilder, pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1, aarrggbb);
        tesselator.end();

        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        renderLine(camera, bufferbuilder, pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, aarrggbb);


        tesselator.end();
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    public static void renderLineSetup(Camera camera, double x1, double y1, double z1, double x2, double y2, double z2, int aarrggbb) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        renderLine(camera, bufferbuilder, x1, y1, z1, x2, y2, z2, aarrggbb);
        tesselator.end();
    }

    public static void renderLine(Camera camera, BufferBuilder bufferBuilder, double x1, double y1, double z1, double x2, double y2, double z2, int rrggbbaa) {
        Vec3 pos = camera.getPosition();
        renderLine(bufferBuilder, x1 - pos.x, y1 - pos.y, z1 - pos.z, x2 - pos.x, y2 - pos.y, z2 - pos.z, rrggbbaa);
    }

    private static void renderLine(BufferBuilder bufferBuilder, double x1, double y1, double z1, double x2, double y2, double z2, int rrggbbaa) {
        bufferBuilder.vertex(x1, y1, z1).color(rrggbbaa).endVertex();
        bufferBuilder.vertex(x2, y2, z2).color(rrggbbaa).endVertex();
    }
}
