package tfar.nabba.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;

public class RepositoryInventoryOutputWrapper implements IItemHandler {

    private AntiBarrelBlockEntity.AntiBarrelInventory internal;

    public RepositoryInventoryOutputWrapper(AntiBarrelBlockEntity.AntiBarrelInventory internal) {
        this.internal = internal;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return internal.getStackInSlot(getSlots() - 1);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return internal.insertItem(slot,stack,simulate);
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
