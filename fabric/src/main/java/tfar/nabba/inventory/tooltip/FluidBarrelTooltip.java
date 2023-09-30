package tfar.nabba.inventory.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidBarrelTooltip implements TooltipComponent {

    private final FluidStack stack;

    public FluidBarrelTooltip(FluidStack stack) {
        this.stack = stack;
    }

    public FluidStack getStack() {
        return stack;
    }
}
