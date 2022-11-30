package tfar.nabba.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class EmptyFluidHandlerItem implements IFluidHandlerItem {

    public static final EmptyFluidHandlerItem INSTANCE = new EmptyFluidHandlerItem();

    @Override
    public @NotNull ItemStack getContainer() {
        return ItemStack.EMPTY;
    }

    @Override
    public int getTanks() {
        return 0;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }
}
