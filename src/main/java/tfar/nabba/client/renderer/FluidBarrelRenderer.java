package tfar.nabba.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.render.FluidTankRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.client.FluidSpriteCache;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.Upgrades;
import tfar.nabba.util.Utils;

public class FluidBarrelRenderer extends AbstractBarrelRenderer<FluidBarrelBlockEntity> {


    public FluidBarrelRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(FluidBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        renderTextAndItems(pBlockEntity, pPoseStack, pBufferSource,pPackedLight,pPackedOverlay);
    }

    protected void renderTextAndItems(FluidBarrelBlockEntity betterBarrelBlockEntity,PoseStack pPoseStack,MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        FluidStack stack = betterBarrelBlockEntity.getFluidHandler().getFluid();

        boolean infiniteVend = betterBarrelBlockEntity.hasUpgrade(Upgrades.INFINITE_VENDING);

        int cap = betterBarrelBlockEntity.getStorage() * 1000;
        String toDraw = infiniteVend ? Utils.INFINITY :stack.getAmount() + " / "+ cap;

        renderText(pPoseStack, bufferSource, pPackedLight, pPackedOverlay,toDraw,14/16d, betterBarrelBlockEntity.getColor(),.0075f);
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof UpgradeItem upgradeItem&& betterBarrelBlockEntity.isValid(upgradeItem)) {
            String slots = betterBarrelBlockEntity.getUsedSlots() + " / " + betterBarrelBlockEntity.getTotalUpgradeSlots();
            renderText(pPoseStack, bufferSource, pPackedLight, pPackedOverlay, slots, 3 / 16d,
                    betterBarrelBlockEntity.canAcceptUpgrade(upgradeItem.getDataStack()) ? 0x00ffff : 0xff0000, .0075f);
        }


        if (stack.isEmpty() && !betterBarrelBlockEntity.hasGhost()) return;

        if (stack.isEmpty())stack = betterBarrelBlockEntity.getGhost();

        renderFluid(betterBarrelBlockEntity,stack, pPoseStack, bufferSource, pPackedLight, pPackedOverlay);
    }
//https://github.com/XFactHD/AdvancedTechnology/blob/1.18.x/src/main/java/xfacthd/advtech/client/render/blockentity/RenderCreativeFluidSource.java
  //  private static final float MIN_WH =  5F/16F; //Width/height of cutout
  //  private static final float MAX_WH = 11F/16F; //Width/height of cutout
    static final float MIN_D = -0.01F/16F; //Depth
    static final float MAX_D = 16.01F/16F; //Depth

    public static int[] splitRGBA(int color)
    {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        return new int[] { r, g, b, a };
    }
    protected void renderFluid(FluidBarrelBlockEntity abstractBarrelBlockEntity, FluidStack fluid, PoseStack pPoseStack, MultiBufferSource buffer, int pPackedLight, int overlay) {

        if (fluid.isEmpty())return;
        float scale = (float) abstractBarrelBlockEntity.getSize();
        if (scale < .01) return;

        final float MIN_WH =  (1 - scale)/2f; //Width/height of cutout
        final float MAX_WH = (scale + 1)/2f; //Width/height of cutout


        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluid);

        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid.getFluid());

        int color =  renderProperties.getTintColor(fluid);

        int[] col = splitRGBA(color);

        VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());
        Matrix4f matrix = pPoseStack.last().pose();
        Matrix3f normal = pPoseStack.last().normal();

        float minU = sprite.getU(0);
        float maxU = sprite.getU(16);
        float minV = sprite.getV(0);
        float maxV = sprite.getV(16);

        //Top
  //      builder.vertex(matrix, MAX_WH, MAX_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
  //      builder.vertex(matrix, MIN_WH, MAX_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
  //      builder.vertex(matrix, MIN_WH, MAX_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
  //      builder.vertex(matrix, MAX_WH, MAX_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();

        //Bottom
   //     builder.vertex(matrix, MIN_WH, MIN_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
   //     builder.vertex(matrix, MAX_WH, MIN_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
   //     builder.vertex(matrix, MAX_WH, MIN_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
   //     builder.vertex(matrix, MIN_WH, MIN_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();

        //North
        builder.vertex(matrix, MAX_WH, MIN_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MIN_WH, MIN_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MIN_WH, MAX_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MAX_WH, MAX_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();

        //South
        builder.vertex(matrix, MIN_WH, MIN_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MAX_WH, MIN_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MAX_WH, MAX_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MIN_WH, MAX_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();

        //East
        builder.vertex(matrix, MAX_D, MIN_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX_D, MIN_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX_D, MAX_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX_D, MAX_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();

        //West
        builder.vertex(matrix, MIN_D, MIN_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN_D, MIN_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN_D, MAX_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN_D, MAX_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
    }

}
