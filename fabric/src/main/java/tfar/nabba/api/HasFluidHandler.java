package tfar.nabba.api;

import org.jetbrains.annotations.NotNull;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.util.FabricFluidStack;

public interface HasFluidHandler extends HasHandler {

    IFluidHandlerShim getFluidHandler();
    default boolean isValid(FabricFluidStack stack) {
        return getFluidHandler().isFluidValid(0,stack);
    }

    default boolean isFull() {
        @NotNull FabricFluidStack fluidVariant = getFluidHandler().getFluidInTank(0);
        return fluidVariant.getAmount() >= getFluidHandler().getTankCapacity(0);
    }
}
