package tfar.nabba.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;

public class RepositoryInventoryInputWrapper implements IItemHandler {

    private AntiBarrelBlockEntity.RepositoryInventory internal;

    public RepositoryInventoryInputWrapper(AntiBarrelBlockEntity.RepositoryInventory internal) {
        this.internal = internal;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    //need to trick the inserting inventory into thinking there's nothing
    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return internal.isFull() ? stack : internal.insertItem(slot,stack,simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internal.extractItem(slot,amount,simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return internal.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return internal.isItemValid(slot,stack);
    }
}
