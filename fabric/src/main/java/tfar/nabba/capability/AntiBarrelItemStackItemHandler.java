package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.NABBAFabric;
import tfar.nabba.api.IItemHandlerItem;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.util.BlockItemBarrelUtils;
import tfar.nabba.util.CommonUtils;
import tfar.nabba.util.ItemStackUtil;
import tfar.nabba.util.NBTKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AntiBarrelItemStackItemHandler implements IItemHandlerItem {
    private final ItemStack container;

    private final List<ItemStack> stacks;
    private UUID uuid;
    private final boolean server;
    
    public AntiBarrelItemStackItemHandler(ItemStack stack) {
        this.container = stack;
        //return a dummy container if we're on the client for some reason
        server = NABBA.server != null;
        uuid = getUUIDFromItem(container);
        stacks = server && uuid != null ? loadItems(NABBAFabric.getData(uuid,NABBA.server).getStorage()) : new ArrayList<>();
    }

    public ItemStack getContainer() {
        return container;
    }


    @Override
    public int getSlots() {
        return stacks.size() + 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (isItemValid(slot, stack)) {
            if (slot == stacks.size()) {
                if (!simulate) {
                    stacks.add(stack);
                    markDirty();
                }
                return ItemStack.EMPTY;
            } else {
                ItemStack existing = getStackInSlot(slot);

                if (!existing.isEmpty() && !ItemStack.isSameItemSameTags(stack,existing)) {
                    if (!simulate) {
                        stacks.add(stack);
                        markDirty();
                    }
                    return ItemStack.EMPTY;
                }

                int slotLimit = getSlotLimit(slot);

                if (existing.getCount() + stack.getCount() > slotLimit) {
                    if (!simulate) {
                        stacks.set(slot, CommonUtils.copyStackWithSize(stack, slotLimit));
                        markDirty();
                    }
                    return BetterBarrelBlockItem.isVoid(container) ? ItemStack.EMPTY :
                            CommonUtils.copyStackWithSize(stack, stack.getCount() + existing.getCount() - slotLimit);
                } else {
                    if (!simulate) {
                        stacks.set(slot, CommonUtils.copyStackWithSize(stack, existing.getCount() + stack.getCount()));
                        markDirty();
                    }
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    public void checkId() {
        if (uuid == null) {
            UUID uuid = UUID.randomUUID();
            container.getOrCreateTag().putUUID(NBTKeys.Uuid.name(), uuid);
            this.uuid = uuid;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || !container.hasTag()) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;
        if (amount >= existing.getCount()) {
            if (!simulate) {
                //remove the item entirely
                stacks.remove(slot);
                markDirty();
            }
            return existing;
        } else {
            if (!simulate) {
                stacks.set(slot, CommonUtils.copyStackWithSize(existing, existing.getCount() - amount));
                markDirty();
            }
            return CommonUtils.copyStackWithSize(existing, amount);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !isFull() && stack.getMaxStackSize() == 1;
    }

    public boolean isFull() {
        return getStoredCount() >= getActualLimit();
    }

    public int getStoredCount() {
        return stacks.stream().mapToInt(ItemStack::getCount).sum();
    }

    public int getActualLimit() {
        return BetterBarrelBlockItem.getStorageMultiplier(container) * NABBAFabric.ServerCfg.anti_barrel_base_storage;
    }

    public void markDirty() {
        if (uuid != null && server) {
            NABBAFabric.getData(uuid,NABBA.server).saveData(saveItems(stacks));
            BlockItemBarrelUtils.getOrCreateBlockEntityTag(container).putInt("Stored", getStoredCount());
        }
    }

    public static ListTag saveItems(List<ItemStack> stacks) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++) {
            CompoundTag itemTag = ItemStackUtil.writeExtendedStack(stacks.get(i));
            nbtTagList.add(itemTag);
        }
        return nbtTagList;
    }

    public static List<ItemStack> loadItems(ListTag tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Tag tag1 : tag) {
            stacks.add(ItemStackUtil.readExtendedItemStack((CompoundTag) tag1));
        }
        return stacks;
    }

    public static UUID getUUIDFromItem(ItemStack antiBarrel) {
        CompoundTag tag = antiBarrel.getTag();
        if (tag != null && tag.contains(NBTKeys.Uuid.name())) {
            return tag.getUUID(NBTKeys.Uuid.name());
        }
        return null;
    }
}