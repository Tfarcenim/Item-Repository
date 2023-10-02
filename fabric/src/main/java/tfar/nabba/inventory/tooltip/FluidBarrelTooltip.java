package tfar.nabba.inventory.tooltip;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import tfar.nabba.client.screen.button.FluidStackWidget;
import tfar.nabba.util.FabricFluidStack;

public class FluidBarrelTooltip implements TooltipComponent {

    private final FabricFluidStack stack;

    public FluidBarrelTooltip(FabricFluidStack stack) {
        this.stack = stack;
    }

    public FabricFluidStack getStack() {
        return stack;
    }
}
