package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class ClientBetterBarrelTooltip extends ClientSlottedTooltip<ItemStack> {
    public ClientBetterBarrelTooltip(BetterBarrelTooltip bundleTooltip) {
        super(bundleTooltip.getStack());
    }

    protected void renderSlot(int i, int j, int slot, Font font, PoseStack poseStack, ItemRenderer itemRenderer, int l) {
        super.renderSlot(i, j, slot, font, poseStack, itemRenderer, l);
        itemRenderer.renderAndDecorateItem(stack, i + 1, j + 1, slot);
        itemRenderer.renderGuiItemDecorations(font, stack, i + 1, j + 1);
    }
}
