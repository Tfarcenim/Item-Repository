package tfar.nabba.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.world.AntiBarrelSubData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AntiBarrelItemStackItemHandler implements IItemHandler, ICapabilityProvider {
    private final ItemStack container;

    private List<ItemStack> stacks;
    private UUID uuid;

    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

    public AntiBarrelItemStackItemHandler(ItemStack stack) {
        this.container = stack;
        stacks = loadItems(getOrCreateSavedData(container));
        uuid = getUUIDFromItem(container);
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
    }

    private void initData() {

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
                if (existing.getCount() + stack.getCount() > getSlotLimit(slot)) {
                    if (!simulate) {
                        stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stack, getSlotLimit(slot)));
                        markDirty();
                    }
                    return BetterBarrelBlockItem.isVoid(container) ? ItemStack.EMPTY :
                            ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() + existing.getCount() - getSlotLimit(slot));
                } else {
                    if (!simulate) {
                        stacks.set(slot,ItemHandlerHelper.copyStackWithSize(stack, existing.getCount() + stack.getCount()));
                        markDirty();
                    }
                    return ItemStack.EMPTY;
                }
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
                stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - amount));
                markDirty();
            }
            return ItemHandlerHelper.copyStackWithSize(existing, amount);
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

    boolean isFull() {
        return getStoredCount() >= BetterBarrelBlockItem.getStorageUnits(container, BarrelType.ANTI);
    }

    public int getStoredCount() {
        return stacks.stream().mapToInt(ItemStack::getCount).sum();
    }

    public void markDirty() {
        NABBA.instance.getData(uuid).saveData(save(stacks));
        BetterBarrelBlockItem.getOrCreateBlockEntityTag(container).putInt("Stored", getStoredCount());
    }

    public static ListTag save(List<ItemStack> stacks) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++) {
            CompoundTag itemTag = new CompoundTag();
            stacks.get(i).save(itemTag);
            nbtTagList.add(itemTag);
        }
        return nbtTagList;
    }

    public static List<ItemStack> loadItems(ListTag tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Tag tag1 : tag) {
            stacks.add(ItemStack.of((CompoundTag) tag1));
        }
        return stacks;
    }

    public static ListTag getOrCreateSavedData(ItemStack antiBarrel) {
        ListTag saveData = getSavedData(antiBarrel);
        if (saveData == null) {
            UUID uuid = UUID.randomUUID();
            antiBarrel.getOrCreateTag().putUUID(NBTKeys.Uuid.name(), uuid);
            NABBA.instance.getData(uuid);
        }
        return getSavedData(antiBarrel);
    }

    public static ListTag getSavedData(ItemStack antiBarrel) {
        UUID uuid = getUUIDFromItem(antiBarrel);
        if (uuid != null) {
            return NABBA.instance.getData(uuid).getStorage();
        }
        return null;
    }

    public static UUID getUUIDFromItem(ItemStack antiBarrel) {
        CompoundTag tag = antiBarrel.getTag();
        if (tag != null && tag.contains(NBTKeys.Uuid.name())) {
            return tag.getUUID(NBTKeys.Uuid.name());
        }
        return null;
    }
}
