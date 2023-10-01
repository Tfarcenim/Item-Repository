package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.util.ClientUtils;

public class ClientFluidBarrelTooltip extends ClientSlottedTooltip<FluidVariant> {
    public ClientFluidBarrelTooltip(FluidBarrelTooltip bundleTooltip) {
        super(bundleTooltip.getStack());
    }

    protected void renderSlot(int i, int j, int slot, Font font, GuiGraphics poseStack) {
        RenderSystem.disableDepthTest();
        super.renderSlot(i, j, slot, font, poseStack);
        ClientUtils.renderFluidTooltip(poseStack,i+1,j+1,stack);
    }
}
