package tfar.nabba.inventory.tooltip;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class FluidBarrelTooltip implements TooltipComponent {

    private final FluidVariant stack;

    public FluidBarrelTooltip(FluidVariant stack) {
        this.stack = stack;
    }

    public FluidVariant getStack() {
        return stack;
    }
}
