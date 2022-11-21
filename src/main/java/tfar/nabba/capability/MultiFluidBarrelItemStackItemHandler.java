package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.item.BetterBarrelBlockItem;
import tfar.nabba.item.FluidBarrelBlockItem;
import tfar.nabba.util.BarrelType;

public class MultiFluidBarrelItemStackItemHandler implements IFluidHandler, ICapabilityProvider {
    private final ItemStack container;

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this);

    public MultiFluidBarrelItemStackItemHandler(ItemStack stack) {
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
        int filled = 0;
        for (int i = 0; i < getTanks();i++) {
            if (isFluidValid(i, stack)) {
                FluidStack existing = getFluidInTank(i);
                if (existing.getAmount() + stack.getAmount() > getTankCapacity(i)) {
                    if (!simulate.simulate()) {
                        FluidBarrelBlockItem.setFluid(container, new FluidStack(stack, getTankCapacity(i)));
                    }
  ///                  return BetterBarrelBlockItem.isVoid(container) ? ItemStack.EMPTY :
//                            ItemHandlerHelper.copyStackWithSize(stack, stack.getAmount() + existing.getAmount() - getTankCapacity(i));
                } else {
                    if (!simulate.simulate()) {
 //                       BetterBarrelBlockItem.setStack(container, ItemHandlerHelper.copyStackWithSize(stack, existing.getAmount() + stack.getAmount()));
                    }
     //               return ItemStack.EMPTY;
                }
            }
        }
        return 0;
      //  return stack;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return null;
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
        return BetterBarrelBlockItem.getStorageUnits(container,BarrelType.FLUID) *
                + (BetterBarrelBlockItem.isVoid(container) ? 1 : 0);
    }

    @Override
    public boolean isFluidValid(int slot, @NotNull FluidStack stack) {
        return FluidBarrelBlockItem.isFluidValid(container,stack);
    }
}
