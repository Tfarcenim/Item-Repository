package tfar.nabba.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.Upgrades;

public class AntiBarrelRenderer extends AbstractBarrelRenderer<AntiBarrelBlockEntity> {


    public AntiBarrelRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(AntiBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        renderTextAndItems(pBlockEntity, pPoseStack, pBufferSource,pPackedLight,pPackedOverlay);
    }

    protected void renderTextAndItems(AntiBarrelBlockEntity antiBarrelBlockEntity,PoseStack pPoseStack,MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        boolean infiniteVend = antiBarrelBlockEntity.hasUpgrade(Upgrades.INFINITE_VENDING);

        int cap = antiBarrelBlockEntity.getStorage();
        String toDraw = infiniteVend ? "\u221E" :antiBarrelBlockEntity.getClientStored() + " / "+ cap;

        renderText(antiBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay, toDraw, 14/16d, antiBarrelBlockEntity.getColor(), .0075f);
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof UpgradeItem upgradeItem && antiBarrelBlockEntity.isValid(upgradeItem)) {
            String slots = antiBarrelBlockEntity.getUsedSlots() + " / " + antiBarrelBlockEntity.getTotalUpgradeSlots();
            renderText(antiBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay, slots,
                    3 / 16d, antiBarrelBlockEntity.canAcceptUpgrade(upgradeItem.getDataStack()) ? 0x00ffff : 0xff0000, .0075f);
        }
        renderItem(antiBarrelBlockEntity,antiBarrelBlockEntity.getLastStack(), pPoseStack, bufferSource, pPackedLight, pPackedOverlay);
    }
}
