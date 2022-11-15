package tfar.nabba.world;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.NABBA;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.inventory.ResizableIItemHandler;
import tfar.nabba.menu.AntiBarrelMenu;

import java.util.ArrayList;
import java.util.List;

public class RepositoryInventory implements IItemHandler, ResizableIItemHandler {

    private AntiBarrelBlockEntity blockEntity;
    public RepositoryInventory() {}
    private List<ItemStack> stacks = new ArrayList<>();

    @Override
    public int getSlots() {
        return isFull() ? stacks.size() : stacks.size() + 1;
    }

    public int getActualStoredCount() {
        return stacks.size();
    }

    public boolean isFull() {
        return getActualStoredCount() >= blockEntity.getStorage();
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
            if (!simulate) {
                ItemStack copy = stack.copy();
                addItem(copy);
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || slot >= getActualStoredCount())
            return ItemStack.EMPTY;
        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;
        ItemStack copy = existing.copy();
        if (!simulate) {
            this.stacks.remove(slot);
            setChanged();
        }
            return copy;
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

    public int getFullSlots(String search) {
        int i = 0;
        for (ItemStack stack : stacks) {
            if (matches(stack,search)){
             i++;
            }
        }
        return i;
    }

    public List<Integer> getDisplaySlots(int row,String search) {
        List<Integer> disp = new ArrayList<>();
        int countForDisplay = 0;
        int index = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            ItemStack stack = getStackInSlot(startPos + index);
            if (matches(stack,search)) {
                disp.add(startPos + index);
                countForDisplay++;
            } else if (stack.isEmpty()) {
                break;
            }
            index++;
        }
        return disp;
    }

    public boolean matches(ItemStack stack,String search) {
        if (search.isEmpty()) {
            return true;
        } else {
            Item item = stack.getItem();
            if (search.startsWith("#")) {
                String sub = search.substring(1);

                List<TagKey<Item>> tags = item.builtInRegistryHolder().tags().toList();
                for (TagKey<Item> tag : tags) {
                    if (tag.location().getPath().startsWith(sub)) {
                        return true;
                    }
                }
                return false;
            } else if (Registry.ITEM.getKey(item).getPath().startsWith(search)) {
                return true;
            }
        }
        return false;
    }

    public void setStacks(List<ItemStack> stacks) {
        this.stacks = stacks;
    }



    public void setChanged() {
        if (NABBA.instance.data != null) {
            NABBA.instance.data.setDirty();
        }
        for (ServerPlayer player : NABBA.instance.server.getPlayerList().getPlayers()) {
            if (player.containerMenu instanceof AntiBarrelMenu antiBarrelMenu && antiBarrelMenu.repositoryInventory == this) {
                antiBarrelMenu.refreshDisplay(player);
            }
        }
        blockEntity.setChanged();
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

    public AntiBarrelBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void setBlockEntity(AntiBarrelBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
}
