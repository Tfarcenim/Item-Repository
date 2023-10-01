package tfar.nabba.api;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import tfar.nabba.shim.IFluidHandlerShim;

public interface HasFluidHandler extends HasHandler{

    IFluidHandlerShim getFluidHandler();
    default boolean isValid(FluidVariant stack) {
        return getFluidHandler().isFluidValid(0,stack);
    }

    default boolean isFull() {
        FluidVariant fluidVariant = getFluidHandler().getFluidInTank(0);
        long amount = fluidVariant.getNbt().getLong("amount");
        return amount >= getFluidHandler().getTankCapacity(0);
    }
}
