package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.inventory.SingleFluidSlotWrapper;

public interface FluidHandler extends IFluidHandler {
    int fill(int tank,int amount,FluidAction action);

    int fill(int tank,FluidStack stack,FluidAction action);


    @NotNull FluidStack drain(int tank, FluidStack resource, FluidAction action);

    @NotNull FluidStack drain(int tank, int maxDrain, FluidAction action);

    FluidActionResult attemptDrainTankWithContainer(int tank, ItemStack container, boolean b);
    default FluidActionResult fillTanksWithContainer(ItemStack container, boolean simulate) {
        return FluidUtil.tryEmptyContainer(container,this,Integer.MAX_VALUE,null,true);
    }

    default FluidActionResult fillTankWithContainer(int tank, ItemStack container, boolean simulate) {
        return FluidUtil.tryEmptyContainer(container,new SingleFluidSlotWrapper(this,tank),Integer.MAX_VALUE,null,true);
    }
}
