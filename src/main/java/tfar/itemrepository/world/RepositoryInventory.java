package tfar.itemrepository.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.itemrepository.ItemRepository;
import tfar.itemrepository.RepositoryMenu;

import java.util.ArrayList;
import java.util.List;

public class RepositoryInventory implements IItemHandler {

    public RepositoryInventory() {}
    private List<ItemStack> stacks = new ArrayList<>();

    @Override
    public int getSlots() {
        return stacks.size();
    }

    public int getStoredCount() {
        return stacks.size();
    }

    public boolean isFull() {
        return getStoredCount() >= 1000000000;
    }

    public void setClientItemStack(int slot, ItemStack stack) {
        stacks.set(slot,stack);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
    }


    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack) || isFull())
            return stack;

        else {
            if (!simulate)
                addItem(stack.copy());
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || slot >= getStoredCount())
            return ItemStack.EMPTY;
        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        if (!simulate) {
            this.stacks.remove(slot);
            setChanged();
            return existing;
        } else {
            return existing.copy();
        }
    }

    public void addItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            stacks.add(stack);
        }
        setChanged();
    }

    public void removeItem() {
        setChanged();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }

    public List<Integer> getDisplaySlots(int row,String search) {
        List<Integer> disp = new ArrayList<>();
        int countForDisplay = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            ItemStack stack = getStackInSlot(startPos + countForDisplay);
            if (matches(stack,search)) {
                disp.add(startPos + countForDisplay);
                countForDisplay++;
            } else if (stack.isEmpty()) {
                break;
            }
        }
        return disp;
    }

    public boolean matches(ItemStack stack,String search) {
        return true;
    }

    public void setStacks(List<ItemStack> stacks) {
        this.stacks = stacks;
    }



    public void setChanged() {
        if (ItemRepository.instance.data != null) {
            ItemRepository.instance.data.setDirty();
        }
        for (ServerPlayer player : ItemRepository.instance.server.getPlayerList().getPlayers()) {
            if (player.containerMenu instanceof RepositoryMenu repositoryMenu && repositoryMenu.repositoryInventory == this) {
                repositoryMenu.refreshDisplay(player);
            }
        }
    }

    public ListTag save() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.stacks.size(); i++) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
        }
        return nbtTagList;
    }

    public void loadItems(ListTag tag) {
        for (Tag tag1 : tag) {
            stacks.add(ItemStack.of((CompoundTag) tag1));
        }
    }

    public boolean isSlotValid(int slot) {
        return slot < getSlots();
    }
}
