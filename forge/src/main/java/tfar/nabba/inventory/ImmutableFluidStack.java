package tfar.nabba.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ImmutableFluidStack extends FluidStack {

    public static FluidStack of(FluidStack stack) {
        return new ImmutableFluidStack(stack.getFluid(),stack.getAmount(),stack.getTag());
    }

    public ImmutableFluidStack(Fluid fluid, int amount) {
        super(fluid, amount);
    }

    public ImmutableFluidStack(Fluid fluid, int amount, CompoundTag nbt) {
        super(fluid, amount, nbt);
    }

    public ImmutableFluidStack(FluidStack stack, int amount) {
        super(stack, amount);
    }


    @Override
    public void setAmount(int amount) {
        throw new UnsupportedOperationException("Something attempted to modify the stack when they shouldn't.");
    }

    @Override
    public void setTag(CompoundTag tag) {
        throw new UnsupportedOperationException("Something attempted to modify the stack when they shouldn't.");
    }
}
