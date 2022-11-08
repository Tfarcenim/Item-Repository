package tfar.itemrepository.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import tfar.itemrepository.blockentity.BetterBarrelBlockEntity;

public class BetterBarrelRenderer implements BlockEntityRenderer<BetterBarrelBlockEntity> {

    private final EntityRenderDispatcher dispatcher;
    private final Font font;
    private final ItemRenderer itemRenderer;

    public BetterBarrelRenderer(BlockEntityRendererProvider.Context pContext) {
        dispatcher = pContext.getEntityRenderer();
        font = pContext.getFont();
        itemRenderer = pContext.getItemRenderer();
    }

    public static final double zFighting = -.0001;

    @Override
    public void render(BetterBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        renderTextAndItems(pBlockEntity, pPoseStack, pBufferSource,pPackedLight,pPackedOverlay);
    }

    protected void renderTextAndItems(BetterBarrelBlockEntity betterBarrelBlockEntity,PoseStack pPoseStack,MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        renderText(betterBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay);
        renderItem(betterBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay);
    }

    protected void renderText(BetterBarrelBlockEntity betterBarrelBlockEntity,PoseStack pPoseStack,MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        ItemStack stack = betterBarrelBlockEntity.getBarrelHandler().getStack();

        int cap = betterBarrelBlockEntity.getStorage() * 64;
        String toDraw = stack.toString()+" / "+ cap;
        int width = font.width(toDraw);
        //text starts in bottom left

        float scale = 1f / width;
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0);
        float f2 = -width / 2f;
        int j = (int)(f1 * 255.0F) << 24;
        int color = 0xff00ff;

        {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5D, 1, zFighting);
            pPoseStack.scale(-scale, -scale, scale);
            Matrix4f matrix4f = pPoseStack.last().pose();
            font.drawInBatch(toDraw, f2 + .5f, 0, color, false, matrix4f, bufferSource, false, j, LightTexture.FULL_BRIGHT);
            pPoseStack.popPose();
        }
        {
            pPoseStack.pushPose();
            pPoseStack.translate(1 - zFighting, 1, .5);
            pPoseStack.scale(-scale, -scale, scale);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(90));
            Matrix4f matrix4f = pPoseStack.last().pose();
            font.drawInBatch(toDraw, f2 + .5f, 0, color, false, matrix4f, bufferSource, false, j, LightTexture.FULL_BRIGHT);
            pPoseStack.popPose();
        }
        {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5D, 1, 1 - zFighting);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            pPoseStack.scale(-scale, -scale, scale);
            Matrix4f matrix4f = pPoseStack.last().pose();
            font.drawInBatch(toDraw, f2 + .5f, 0, color, false, matrix4f, bufferSource, false, j, LightTexture.FULL_BRIGHT);
            pPoseStack.popPose();
        }
        {
            pPoseStack.pushPose();
            pPoseStack.translate(zFighting, 1, .5);
            pPoseStack.scale(-scale, -scale, scale);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(270));
            Matrix4f matrix4f = pPoseStack.last().pose();
            font.drawInBatch(toDraw, f2 + .5f, 0, color, false, matrix4f, bufferSource, false, j, LightTexture.FULL_BRIGHT);
            pPoseStack.popPose();
        }
    }

    protected void renderItem(BetterBarrelBlockEntity betterBarrelBlockEntity,PoseStack pPoseStack,MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        ItemStack stack = betterBarrelBlockEntity.getBarrelHandler().getStack();
        if (stack.isEmpty()) return;
        try {
            pPoseStack.pushPose();
            pPoseStack.translate(.5,.5,zFighting);

            float scale = .5f;
            pPoseStack.mulPoseMatrix(Matrix4f.createScaleMatrix(scale,scale, 0.001f));

            BakedModel bakedmodel = this.itemRenderer.getModel(stack, betterBarrelBlockEntity.getLevel(), null, 0);
            itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, pPoseStack, bufferSource, LightTexture.FULL_BRIGHT, pPackedOverlay, bakedmodel);

            if (bakedmodel.isGui3d())
                pPoseStack.last().normal().mul(ITEM_LIGHT_ROTATION_3D);
            else
                pPoseStack.last().normal().mul(ITEM_LIGHT_ROTATION_FLAT);

        } catch (Exception e) {
            //bruh
        }
        pPoseStack.popPose();
    }


    private static final Quaternion ITEM_LIGHT_ROTATION_3D = Util.make(() -> {
        Quaternion quaternion = new Quaternion(Vector3f.XP, -15f, true);
        quaternion.mul(new Quaternion(Vector3f.YP, 15f, true));
        return quaternion;
    });
    private static final Quaternion ITEM_LIGHT_ROTATION_FLAT = new Quaternion(Vector3f.XP, -45f, true);

}
