package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.util.ClientUtils;

public class ClientFluidBarrelTooltip extends ClientSlottedTooltip<FluidStack> {
    public ClientFluidBarrelTooltip(FluidBarrelTooltip bundleTooltip) {
        super(bundleTooltip.getStack());
    }

    protected void renderSlot(int i, int j, int slot, Font font, PoseStack poseStack, ItemRenderer itemRenderer, int l) {
        RenderSystem.disableDepthTest();
        super.renderSlot(i, j, slot, font, poseStack, itemRenderer, l);
        ClientUtils.renderFluidTooltip(poseStack,i+1,j+1,stack);
    }
}
