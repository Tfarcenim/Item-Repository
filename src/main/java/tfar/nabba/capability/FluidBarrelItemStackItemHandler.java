package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.item.barrels.FluidBarrelBlockItem;
import tfar.nabba.util.BarrelType;

public class FluidBarrelItemStackItemHandler implements IFluidHandler, ICapabilityProvider {
    private final ItemStack container;

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this);

    public FluidBarrelItemStackItemHandler(ItemStack stack) {
        this.container = stack;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, holder);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int slot) {
        return FluidBarrelBlockItem.getStoredFluid(container);
    }

    @Override
    public int fill(@NotNull FluidStack stack, FluidAction simulate) {
            if (isFluidValid(0, stack)) {
                int limit = getTankCapacity(0);
                FluidStack existing = getFluidInTank(0);
                if (existing.getAmount() + stack.getAmount() >= limit) {
                    if (!simulate.simulate()) {
                        FluidBarrelBlockItem.setFluid(container, new FluidStack(stack, limit));
                    }
                    return BetterBarrelBlockItem.isVoid(container) ? stack.getAmount() : limit - existing.getAmount();
                } else {
                    if (!simulate.simulate()) {
                        FluidBarrelBlockItem.setFluid(container, new FluidStack(stack, existing.getAmount() + stack.getAmount()));
                    }
                    return existing.getAmount()+stack.getAmount();
                }
            }
            return 0;
        }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack existing = getFluidInTank(0);
        if (existing.isFluidEqual(resource))
            return drain(resource.getAmount(), action);
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int amount, FluidAction simulate) {
        if (amount == 0 || !container.hasTag()) {
            return FluidStack.EMPTY;
        }

        for (int i = 0; i < getTanks();i++) {
            FluidStack existing = getFluidInTank(i);
            if (amount >= existing.getAmount()) {
                if (!simulate.simulate()) {
                    FluidBarrelBlockItem.setFluid(container, FluidStack.EMPTY);
                }
                return existing;
            } else {
                if (!simulate.simulate()) {
                    FluidBarrelBlockItem.setFluid(container, new FluidStack(existing, existing.getAmount() - amount));
                }
                return new FluidStack(existing, amount);
            }
        }
        return FluidStack.EMPTY;
    }


    @Override
    public int getTankCapacity(int slot) {
        return BetterBarrelBlockItem.getStorageUnits(container,BarrelType.FLUID) * 1000 + (BetterBarrelBlockItem.isVoid(container) ? 1 : 0);
    }

    @Override
    public boolean isFluidValid(int slot, @NotNull FluidStack stack) {
        return FluidBarrelBlockItem.isFluidValid(container,stack);
    }
}
