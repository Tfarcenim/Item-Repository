package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.NABBAFabric;
import tfar.nabba.api.IItemHandlerItem;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.util.BlockItemBarrelUtils;
import tfar.nabba.util.CommonUtils;

public class BetterBarrelItemStackItemHandler implements IItemHandlerItem {
    private final ItemStack container;


    public BetterBarrelItemStackItemHandler(ItemStack stack) {
        this.container = stack;
    }

    public ItemStack getContainer() {
        return container;
    }
    

    @Override
    public int getSlots() {
        return container.getCount();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return BlockItemBarrelUtils.getStoredItem(container);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (isItemValid(slot,stack)) {
            ItemStack existing = getStackInSlot(slot);
            if (existing.getCount() + stack.getCount() > getSlotLimit(slot)) {
                if (!simulate) {
                    BlockItemBarrelUtils.setStack(container, CommonUtils.copyStackWithSize(stack, getSlotLimit(slot)));
                }
                return BetterBarrelBlockItem.isVoid(container) ? ItemStack.EMPTY :
                        CommonUtils.copyStackWithSize(stack, stack.getCount() + existing.getCount() - getSlotLimit(slot));
            } else {
                if (!simulate) {
                    BlockItemBarrelUtils.setStack(container, CommonUtils.copyStackWithSize(stack, existing.getCount() + stack.getCount()));
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
                BlockItemBarrelUtils.setStack(container, ItemStack.EMPTY);
            }
            return existing;
        } else {
            if (!simulate) {
                BlockItemBarrelUtils.setStack(container, CommonUtils.copyStackWithSize(existing,existing.getCount() - amount));
            }
            return CommonUtils.copyStackWithSize(existing,amount);
        }
    }


    @Override
    public int getSlotLimit(int slot) {
        return getActualLimit(slot) + (BetterBarrelBlockItem.isVoid(container) ? 1 : 0);
    }

    public int getActualLimit(int slot) {
        ItemStack existing = getStackInSlot(slot);
        return BetterBarrelBlockItem.getStorageMultiplier(container) * existing.getMaxStackSize() *
                (BetterBarrelBlockItem.storageDowngrade(container) ? 1 : NABBAFabric.ServerCfg.better_barrel_base_storage);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return BlockItemBarrelUtils.isItemValid(container,stack);
    }
}
