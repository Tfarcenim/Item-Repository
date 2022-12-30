package tfar.nabba.blockentity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.NABBA;
import tfar.nabba.api.HasItemHandler;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.inventory.ResizableIItemHandler;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.net.util.ItemStackUtil;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AntiBarrelBlockEntity extends AbstractBarrelBlockEntity implements MenuProvider, HasItemHandler, HasSearchBar {

    private UUID uuid = Util.NIL_UUID;
    private Component customName;

    private String search = "";

    private ItemStack last = ItemStack.EMPTY;
    private int clientStored;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return getInventory().getStoredCount();
                case 1:
                    return getInventory().getFullItemSlots(search);
                default:
                    return 0;
            }
        }

        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0:
            }
        }

        public int getCount() {
            return 2;
        }
    };

    protected final int[] syncSlots = new int[54];

    private static void defaultDisplaySlots(int[] ints) {
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
    }

    protected final ContainerData syncSlotsAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return syncSlots[pIndex];
        }

        @Override
        public void set(int pIndex, int pValue) {
            syncSlots[pIndex] = pValue;
        }

        @Override
        public int getCount() {
            return syncSlots.length;
        }
    };

    public AntiBarrelInventory getInventory() {
        return NABBA.instance.getData(uuid,level.getServer()).getInventory(this);
    }

    public static AntiBarrelBlockEntity create(BlockPos pos, BlockState state) {
        return new AntiBarrelBlockEntity(ModBlockEntityTypes.ANTI_BARREL, pos, state);
    }

    public static AntiBarrelBlockEntity createDiscrete(BlockPos pos, BlockState state) {
        return new AntiBarrelBlockEntity(ModBlockEntityTypes.DISCRETE_ANTI_BARREL, pos, state);
    }

    public AntiBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        defaultDisplaySlots(syncSlots);
    }

    public AntiBarrelBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.ANTI_BARREL, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AntiBarrelMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(level, getBlockPos()), getInventory(), dataAccess, syncSlotsAccess);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(this::getInventory).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public AntiBarrelInventory getItemHandler() {
        return getInventory();
    }

    public void saveAdditional(CompoundTag tag) {
        if (getUuid() != Util.NIL_UUID) {
            tag.putUUID(NBTKeys.Uuid.name(), getUuid());
        }
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        tag.putInt("Stored", clientStored);
        tag.put("Last", last.save(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override//read
    public void load(CompoundTag tag) {
        if (tag.hasUUID(NBTKeys.Uuid.name()) && tag.getUUID(NBTKeys.Uuid.name()) != Util.NIL_UUID) {
            setUuid(tag.getUUID(NBTKeys.Uuid.name()));
        }
        if (tag.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
        clientStored = tag.getInt("Stored");
        last = ItemStack.of(tag.getCompound("Last"));
        super.load(tag);
    }

    public void setCustomName(Component hoverName) {
        customName = hoverName;
    }

    void setClientCountAndLast(ItemStack last, int count) {
        clientStored = count;
        this.last = last;
        setChanged();
    }

    public ItemStack getLastStack() {
        return last;
    }

    public int getClientStored() {
        return clientStored;
    }

    //make sure a new id is set if there isn't one and create an inventory for it so the game doesn't freak out
    public void initialize(ItemStack stack) {
        if (uuid == Util.NIL_UUID) {
            if (stack.hasTag() && stack.getTag().hasUUID(NBTKeys.Uuid.name()) && !stack.getTag().getUUID(NBTKeys.Uuid.name()).equals(Util.NIL_UUID)) {
                setUuid(stack.getTag().getUUID(NBTKeys.Uuid.name()));
            } else {
                setUuid(UUID.randomUUID());
            }
            setChanged();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setSearchString(String search) {
        this.search = search;
    }

    @Override
    public String getSearchString() {
        return search;
    }

    @Override
    public int getRedstoneOutput() {
        return Utils.getRedstoneSignalFromAntibarrel(getInventory());
    }

    public static class AntiBarrelInventory implements SearchableItemHandler, ResizableIItemHandler {

        private final AntiBarrelBlockEntity blockEntity;

        public AntiBarrelInventory(AntiBarrelBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }


        public AntiBarrelBlockEntity getBlockEntity() {
            return blockEntity;
        }

        private List<ItemStack> stacks = new ArrayList<>();

        @Override
        public int getSlots() {
            return isFull() ? stacks.size() : stacks.size() + 1;
        }

        public int getStoredCount() {
            return stacks.stream().mapToInt(ItemStack::getCount).sum();
        }

        public boolean isFull() {
            return getStoredCount() >= getActualLimit();
        }

        public int getActualLimit() {
            return blockEntity.getStorageMultiplier() * NABBA.ServerCfg.anti_barrel_base_storage.get();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (isFull())
                return stack;

            else if (slot == stacks.size()) {
                if (!simulate) {
                    stacks.add(stack);
                    setChanged();
                }
                return ItemStack.EMPTY;
            } else {
                ItemStack existing = getStackInSlot(slot);
                if (existing.isEmpty()) {
                    if (!simulate) {
                        stacks.set(slot, stack);
                        setChanged();
                    }
                    return ItemStack.EMPTY;
                } else {
                    if (ItemStack.isSameItemSameTags(stack, existing)) {
                        if (!simulate) {
                            existing.grow(stack.getCount());
                            setChanged();
                        }
                        return ItemStack.EMPTY;
                    } else {
                        return stack;
                    }
                }
            }
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0 || slot >= getStoredCount())
                return ItemStack.EMPTY;
            ItemStack existing = this.stacks.get(slot);

            if (existing.isEmpty())
                return ItemStack.EMPTY;

            if (amount >= existing.getCount()) {

                if (!simulate) {
                    stacks.remove(slot);
                    setChanged();
                }
                return existing;
            } else {
                if (!simulate) {
                    existing.shrink(amount);
                    setChanged();
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
            return stack.getMaxStackSize() == 1;
        }

        public List<ItemStack> getStacks() {
            return stacks;
        }

        public void setStacks(List<ItemStack> stacks) {
            this.stacks = stacks;
        }

        ItemStack getLastItem() {
            int stored = getStoredCount();
            return stored > 0 ? getStackInSlot(getSlots() - 1) : ItemStack.EMPTY;
        }

        public void setChanged() {
            NABBA.instance.getData(blockEntity.getUuid(), blockEntity.getLevel().getServer()).writeInvData(this);
            blockEntity.setClientCountAndLast(getLastItem(), getStoredCount());
        }

        public ListTag save() {
            ListTag nbtTagList = new ListTag();
            for (int i = 0; i < this.stacks.size(); i++) {
                CompoundTag itemTag = ItemStackUtil.writeExtendedStack(stacks.get(i));
                nbtTagList.add(itemTag);
            }
            return nbtTagList;
        }

        public void loadItems(ListTag tag) {
            stacks.clear();
            for (Tag tag1 : tag) {
                stacks.add(ItemStackUtil.readExtendedItemStack((CompoundTag) tag1));
            }
        }

        public boolean isSlotValid(int slot) {
            return slot < getSlots();
        }
    }
}
