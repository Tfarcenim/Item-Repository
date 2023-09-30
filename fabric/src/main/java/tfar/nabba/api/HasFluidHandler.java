package tfar.nabba.api;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface HasFluidHandler extends HasHandler{

    IFluidHandler getFluidHandler();
    default boolean isValid(FluidStack stack){
        return getFluidHandler().isFluidValid(0,stack);
    }

    default boolean isFull() {
        return getFluidHandler().getFluidInTank(0).getAmount() >= getFluidHandler().getTankCapacity(0);
    }
}
