package tfar.nabba.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ClientBetterBarrelTooltip extends ClientSlottedTooltip<ItemStack> {
    public ClientBetterBarrelTooltip(BetterBarrelTooltip bundleTooltip) {
        super(bundleTooltip.getStack());
    }

    protected void renderSlot(int i, int j, int slot, Font font, GuiGraphics poseStack) {
        super.renderSlot(i, j, slot, font, poseStack);
        poseStack.renderItem(stack, i + 1, j + 1, slot);
        poseStack.renderItemDecorations(font, stack, i + 1, j + 1);
    }
}
