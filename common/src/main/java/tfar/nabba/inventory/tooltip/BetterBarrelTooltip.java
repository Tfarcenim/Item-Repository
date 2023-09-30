package tfar.nabba.inventory.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BetterBarrelTooltip implements TooltipComponent {

    private final ItemStack stack;

    public BetterBarrelTooltip(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }
}
