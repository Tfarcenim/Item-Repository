package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import tfar.nabba.util.ClientUtils;
import tfar.nabba.util.FabricFluidStack;

public class ClientFluidBarrelTooltip extends ClientSlottedTooltip<FabricFluidStack> {
    public ClientFluidBarrelTooltip(FluidBarrelTooltip bundleTooltip) {
        super(bundleTooltip.getStack());
    }

    protected void renderSlot(int i, int j, int slot, Font font, GuiGraphics poseStack) {
        RenderSystem.disableDepthTest();
        super.renderSlot(i, j, slot, font, poseStack);
        ClientUtils.renderFluidTooltip(poseStack,i+1,j+1,stack);
    }
}
