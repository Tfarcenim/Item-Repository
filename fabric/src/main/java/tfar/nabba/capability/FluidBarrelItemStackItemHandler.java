package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.inventory.ImmutableFabricFluidStack;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.item.barrels.FluidBarrelBlockItem;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.util.FabricFluidStack;

public class FluidBarrelItemStackItemHandler implements IFluidHandlerItem, ICapabilityProvider {
    private final ItemStack container;

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    public FluidBarrelItemStackItemHandler(ItemStack stack) {
        this.container = stack;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, holder);
    }

    @Override
    public int getTanks() {
        return container.getCount();
    }

    @Override
    public @NotNull FabricFluidStack getFluidInTank(int slot) {
        return ImmutableFabricFluidStack.of(FluidBarrelBlockItem.getStoredFluid(container));
    }

    @Override
    public long fill(@NotNull FabricFluidStack stack, IFluidHandlerShim.FluidAction simulate) {
            if (isFluidValid(0, stack)) {
                int limit = getTankCapacity(0);
                FabricFluidStack existing = getFluidInTank(0);

                if (existing.getAmount()>= limit) {
                    return BetterBarrelBlockItem.isVoid(container) ? stack.getAmount() : 0;
                }

                if (existing.getAmount() + stack.getAmount() >= limit) {
                    if (!simulate.simulate()) {
                        FluidBarrelBlockItem.setFluid(container, new FabricFluidStack(stack, limit));
                    }
                    return BetterBarrelBlockItem.isVoid(container) ? stack.getAmount() : limit - existing.getAmount();
                } else {
                    if (!simulate.simulate()) {
                        FluidBarrelBlockItem.setFluid(container, new FabricFluidStack(stack, existing.getAmount() + stack.getAmount()));
                    }
                    return stack.getAmount();
                }
            }
            return 0;
        }

    @Override
    public @NotNull FabricFluidStack drain(FabricFluidStack resource, IFluidHandlerShim.FluidAction action) {
        FabricFluidStack existing = getFluidInTank(0);
        if (existing.isFluidEqual(resource))
            return drain(resource.getAmount(), action);
        return FabricFluidStack.EMPTY;
    }

    @Override
    public @NotNull FabricFluidStack drain(long amount, IFluidHandlerShim.FluidAction simulate) {
        if (amount == 0 || !container.hasTag()) {
            return FabricFluidStack.empty();
        }

        for (int i = 0; i < getTanks();i++) {
            FabricFluidStack existing = getFluidInTank(i);

            if (BetterBarrelBlockItem.infiniteVending(container)) {
                return new FabricFluidStack(existing,amount);
            }

            if (amount >= existing.getAmount()) {
                if (!simulate.simulate()) {
                    FluidBarrelBlockItem.setFluid(container, FabricFluidStack.EMPTY);
                }
                return existing.copy();
            } else {
                if (!simulate.simulate()) {
                    FluidBarrelBlockItem.setFluid(container, new FabricFluidStack(existing, existing.getAmount() - amount));
                }
                return new FabricFluidStack(existing, amount);
            }
        }
        return FabricFluidStack.EMPTY;
    }


    @Override
    public int getTankCapacity(int slot) {
        return getActualLimit(slot) + (BetterBarrelBlockItem.isVoid(container) ? 1 : 0);
    }

    public int getActualLimit(int slot) {
        return BetterBarrelBlockItem.getStorageMultiplier(container) * 1000 *
                (BetterBarrelBlockItem.storageDowngrade(container) ? 1 : NABBA.ServerCfg.fluid_barrel_base_storage.get());
    }


    @Override
    public boolean isFluidValid(int slot, @NotNull FabricFluidStack stack) {
        return FluidBarrelBlockItem.isFluidValid(container,stack);
    }
}
