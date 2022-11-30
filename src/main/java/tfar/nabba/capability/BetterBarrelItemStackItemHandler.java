package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.IItemHandlerItem;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.util.BarrelType;

public class BetterBarrelItemStackItemHandler implements IItemHandlerItem, ICapabilityProvider {
    private final ItemStack container;

    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

    public BetterBarrelItemStackItemHandler(ItemStack stack) {
        this.container = stack;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
    }

    @Override
    public int getSlots() {
        return container.getCount();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return BetterBarrelBlockItem.getStoredItem(container);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (isItemValid(slot,stack)) {
            ItemStack existing = getStackInSlot(slot);
            if (existing.getCount() + stack.getCount() > getSlotLimit(slot)) {
                if (!simulate) {
                    BetterBarrelBlockItem.setStack(container, ItemHandlerHelper.copyStackWithSize(stack, getSlotLimit(slot)));
                }
                return BetterBarrelBlockItem.isVoid(container) ? ItemStack.EMPTY :
                        ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() + existing.getCount() - getSlotLimit(slot));
            } else {
                if (!simulate) {
                    BetterBarrelBlockItem.setStack(container, ItemHandlerHelper.copyStackWithSize(stack, existing.getCount() + stack.getCount()));
                }
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || !container.hasTag()) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty())return ItemStack.EMPTY;
        if (amount >= existing.getCount()) {
            if (!simulate) {
                BetterBarrelBlockItem.setStack(container, ItemStack.EMPTY);
            }
            return existing;
        } else {
            if (!simulate) {
                BetterBarrelBlockItem.setStack(container, ItemHandlerHelper.copyStackWithSize(existing,existing.getCount() - amount));
            }
            return ItemHandlerHelper.copyStackWithSize(existing,amount);
        }
    }


    @Override
    public int getSlotLimit(int slot) {
        ItemStack existing = getStackInSlot(slot);
        return BetterBarrelBlockItem.getStorageUnits(container,BarrelType.BETTER) * existing.getMaxStackSize()
                + (BetterBarrelBlockItem.isVoid(container) ? 1 : 0);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return BetterBarrelBlockItem.isItemValid(container,stack);
    }
}
