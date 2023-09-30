package tfar.nabba.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.Upgrades;
import tfar.nabba.util.Utils;

public class BetterBarrelRenderer extends AbstractBarrelRenderer<BetterBarrelBlockEntity> {


    public BetterBarrelRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BetterBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        renderTextAndItems(pBlockEntity, pPoseStack, pBufferSource,pPackedLight,pPackedOverlay);
    }

    protected void renderTextAndItems(BetterBarrelBlockEntity betterBarrelBlockEntity,PoseStack pPoseStack,MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        ItemStack stack = betterBarrelBlockEntity.getItemHandler().getStack();

        boolean infiniteVend = betterBarrelBlockEntity.infiniteVending();

        int cap = betterBarrelBlockEntity.getItemHandler().getActualLimit();
        String toDraw = infiniteVend ? Utils.INFINITY :stack.getCount() + " / "+ cap;

        renderText(betterBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay, toDraw, 14/16d, betterBarrelBlockEntity.getColor(), .0075f);
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof UpgradeItem upgradeItem&& betterBarrelBlockEntity.isValid(upgradeItem)) {
            String slots = betterBarrelBlockEntity.getUsedSlots() + " / " + betterBarrelBlockEntity.getTotalUpgradeSlots();
            renderText(betterBarrelBlockEntity, pPoseStack, bufferSource, pPackedLight, pPackedOverlay, slots,
                    3 / 16d, betterBarrelBlockEntity.canAcceptUpgrade(upgradeItem.getDataStack()) ? 0x00ffff : 0xff0000, .0075f);
        }


        if (stack.isEmpty() && !betterBarrelBlockEntity.hasGhost()) return;

        if (stack.isEmpty())stack = betterBarrelBlockEntity.getGhost();

        renderItem(betterBarrelBlockEntity,stack, pPoseStack, bufferSource, pPackedLight, pPackedOverlay);
    }
}
