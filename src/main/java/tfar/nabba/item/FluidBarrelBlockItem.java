package tfar.nabba.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.inventory.tooltip.FluidBarrelTooltip;

import java.util.Optional;

public class FluidBarrelBlockItem extends BlockItem {
    public FluidBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        FluidStack disp = getStoredFluid(pStack);
        return disp.isEmpty() ? super.getTooltipImage(pStack) : Optional.of(new FluidBarrelTooltip(disp));
    }

    public static FluidStack getStoredFluid(ItemStack barrel) {
        if (barrel.hasTag() && barrel.getTag().contains("BlockEntityTag")) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(barrel.getTag().getCompound("BlockEntityTag").getCompound("Stack"));
            return stack;
        }
        return FluidStack.EMPTY;
    }
}
