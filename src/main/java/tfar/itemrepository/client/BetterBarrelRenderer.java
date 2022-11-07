package tfar.itemrepository.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import tfar.itemrepository.blockentity.BetterBarrelBlockEntity;

public class BetterBarrelRenderer implements BlockEntityRenderer<BetterBarrelBlockEntity> {

    private final EntityRenderDispatcher dispatcher;
    private final Font font;
    public BetterBarrelRenderer(BlockEntityRendererProvider.Context pContext) {
        dispatcher = pContext.getEntityRenderer();
        font = pContext.getFont();
    }

    @Override
    public void render(BetterBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        ItemStack stack = pBlockEntity.getBarrelHandler().getStack();
        String name = stack.toString();

        pPoseStack.translate(0.5D, 1.5, 0.5D);

        pPoseStack.mulPose(dispatcher.cameraOrientation());
        pPoseStack.scale(-0.025F, -0.025F, 0.025F);
        float f2 = (float)(-font.width(name) / 2);
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int)(f1 * 255.0F) << 24;
        font.drawInBatch(name, f2,0, 0x20ffffff, false, matrix4f, pBufferSource, false, j, pPackedLight);
        pPoseStack.popPose();
    }
}
