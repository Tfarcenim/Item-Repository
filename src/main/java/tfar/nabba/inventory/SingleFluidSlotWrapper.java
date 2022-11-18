package tfar.nabba.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.api.FluidHandler;

public class SingleFluidSlotWrapper implements IFluidHandler {

    private final FluidHandler handler;
    private final int tankNumber;

    public SingleFluidSlotWrapper(FluidHandler handler, int tankNumber) {
        this.handler = handler;
        this.tankNumber = tankNumber;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return handler.getFluidInTank(tankNumber);
    }

    @Override
    public int getTankCapacity(int tank) {
        return handler.getTankCapacity(tankNumber);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return handler.isFluidValid(tankNumber,stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return handler.fill(tankNumber,resource,action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return handler.drain(tankNumber,resource,action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return handler.drain(tankNumber,maxDrain,action);
    }
}
