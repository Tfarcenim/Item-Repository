package tfar.nabba.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.client.FluidSpriteCache;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.CommonUtils;
import tfar.nabba.util.FabricFluidStack;

import javax.annotation.Nullable;

public class FluidBarrelRenderer extends AbstractBarrelRenderer<FluidBarrelBlockEntity> {


    public FluidBarrelRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(FluidBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        renderTextAndItems(pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
    }

    protected void renderTextAndItems(FluidBarrelBlockEntity betterBarrelBlockEntity, PoseStack pPoseStack, MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        FabricFluidStack stack = betterBarrelBlockEntity.getFluidHandler().getFluid();

        boolean infiniteVend = betterBarrelBlockEntity.infiniteVending();

        long cap = betterBarrelBlockEntity.getFluidHandler().getActualCapacity(0) / 81;
        String toDraw = infiniteVend ? CommonUtils.INFINITY : stack.getAmount() / 81 + " / " + cap + " mB";

        renderText(betterBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay, toDraw, 14 / 16d, betterBarrelBlockEntity.getColor(), .0075f);
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof UpgradeItem upgradeItem && betterBarrelBlockEntity.isValid(upgradeItem)) {
            String slots = betterBarrelBlockEntity.getUsedSlots() + " / " + betterBarrelBlockEntity.getTotalUpgradeSlots();
            renderText(betterBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay, slots,
                    3 / 16d, betterBarrelBlockEntity.canAcceptUpgrade(upgradeItem.getDataStack()) ? 0x00ffff : 0xff0000, .0075f);
        }


        if (stack.isEmpty() && !betterBarrelBlockEntity.hasGhost()) return;

        if (stack.isEmpty()) stack = betterBarrelBlockEntity.getGhost();

        renderFluid(betterBarrelBlockEntity, stack, pPoseStack, bufferSource, pPackedLight, pPackedOverlay);
    }

    //https://github.com/XFactHD/AdvancedTechnology/blob/1.18.x/src/main/java/xfacthd/advtech/client/render/blockentity/RenderCreativeFluidSource.java
    //  private static final float MIN_WH =  5F/16F; //Width/height of cutout
    //  private static final float MAX_WH = 11F/16F; //Width/height of cutout
    static final float MIN_D = -0.01F / 16F; //Depth
    static final float MAX_D = 16.01F / 16F; //Depth

    public static int[] splitRGBA(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        return new int[]{r, g, b, a};
    }

    protected void renderFluid(FluidBarrelBlockEntity abstractBarrelBlockEntity, FabricFluidStack fluid, PoseStack pPoseStack, MultiBufferSource buffer, int pPackedLight, int overlay) {

        if (fluid.isEmpty()) return;
        float scale = (float) abstractBarrelBlockEntity.getSize();
        if (scale < .01) return;

        Direction facing = abstractBarrelBlockEntity.getBlockState().getValue(AbstractBarrelBlock.H_FACING);

        drawFluidInTank(fluid.getFluidVariant(), scale, pPoseStack, buffer, abstractBarrelBlockEntity.getLevel(), null, facing);
    }

    /*
     * Renders fluids inside Portable Tanks.
     * This code is derivative of the one found in Modern Industrialization, copyrighted by Azercoco & Technici4n and licensed under MIT.
     *
     * You may see the original code here: https://github.com/AztechMC/Modern-Industrialization/blob/8e1be7d3b607614ded24f60ec5927d97c6649cc9/src/main/java/aztech/modern_industrialization/util/RenderHelper.java#L124
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void drawFluidInTank(FluidVariant fluid, float scale, PoseStack ms, MultiBufferSource vcp, @Nullable Level world, @Nullable BlockPos pos, Direction direction) {
        VertexConsumer vc = vcp.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = FluidVariantRendering.getSprite(fluid);
        if (sprite == null) return;

        int color = (world == null && pos == null) ? FluidVariantRendering.getColor(fluid, null, null) : FluidVariantRendering.getColor(fluid, world, pos);
        float r = ((color >> 16) & 255) / 256f;
        float g = ((color >> 8) & 255) / 256f;
        float b = (color & 255) / 256f;

        // Top and bottom positions of the fluid inside the tank
        float topHeight = (scale + 1)/2;
        float bottomHeight = (1 - scale) / 2;
        // Render gas from top to bottom

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        QuadEmitter emitter = renderer.meshBuilder().getEmitter();

        emitter.square(direction, .25f, bottomHeight, .75f, topHeight, -1 / 256f);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);

        vc.putBulkData(ms.last(), emitter.toBakedQuad(0, sprite, false), r, g, b, FULL_LIGHT, LightTexture.FULL_BLOCK);
    }

    public static final int FULL_LIGHT = 0x00F0_00F0;


    public static final float TANK_W = 0.07F;
    public static final float TANK_START = 0.16F;
    public static final float TANK_FINAL = 0.84F;


}
