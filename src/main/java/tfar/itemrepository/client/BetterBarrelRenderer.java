package tfar.itemrepository.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
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

        int cap = pBlockEntity.getStorage() * 64;
        String toDraw = stack.toString()+" / "+ cap;
        int width = font.width(toDraw);
        //text starts in bottom left
        pPoseStack.translate(0.5D, 1,-0.0001);//zfighting

        float scale = 1f / width;

        pPoseStack.scale(-scale, -scale, scale);
        float f2 = -width / 2f;
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0);
        int j = (int)(f1 * 255.0F) << 24;
        font.drawInBatch(toDraw , f2+.5f,0, 0xff00ff, false, matrix4f, pBufferSource, false, j, LightTexture.FULL_BRIGHT);
        pPoseStack.popPose();
    }
}
