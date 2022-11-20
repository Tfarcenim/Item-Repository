package tfar.nabba.blockentity;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModItemTags;
import tfar.nabba.menu.BarrelInterfaceMenu;

import java.util.ArrayList;
import java.util.List;

public class BarrelInterfaceBlockEntity extends BlockEntity implements MenuProvider, HasSearchBar {

    private BarrelInterfaceItemHandler handler = new BarrelInterfaceItemHandler(this);
    private BarrelWrapper wrapper = new BarrelWrapper(this);
    private String search = "";

    public BarrelInterfaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(ModBlockEntityTypes.BARREL_INTERFACE, pPos, pBlockState);
    }

    public BarrelInterfaceBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return getHandler().getStoredCount();
                case 1:
                    return getHandler().getFullSlots(search);
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

    public BarrelInterfaceItemHandler getHandler() {
        return handler;
    }

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

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BarrelInterfaceMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(level, worldPosition), handler, dataAccess, syncSlotsAccess);
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
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Items", getHandler().serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        getHandler().deserializeNBT(pTag.getList("Items", Tag.TAG_COMPOUND));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            wrapper.recomputeSlots();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? LazyOptional.of(() -> wrapper).cast() : super.getCapability(cap, side);
    }

    public static final int SIZE = 256;

    public static class BarrelWrapper implements IItemHandler {

        private BarrelInterfaceBlockEntity blockEntity;

        protected List<Integer> baseItemHandlerIndices = new ArrayList<>();
        protected int totalItemSlotCount;
        protected List<LazyOptional<IItemHandler>> itemHandlers; // the handlers



        public BarrelWrapper(BarrelInterfaceBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        public void recomputeSlots() {
            itemHandlers = itemCaps();
            baseItemHandlerIndices.clear();
            int index = 0;
            for (int i = 0; i < itemHandlers.size(); i++) {
                index += itemHandlers.get(i).map(IItemHandler::getSlots).orElse(0);
                baseItemHandlerIndices.add(index);
            }
            this.totalItemSlotCount = index;
        }

        public List<LazyOptional<IItemHandler>> itemCaps() {
            return caps(ForgeCapabilities.ITEM_HANDLER);
        }

        public <T> List<LazyOptional<T>> caps(Capability<T> capability) {
            List<LazyOptional<T>> list = new ArrayList<>();
            for (ItemStack stack : getBarrelInt().barrels) {
                if (stack.getCapability(capability).isPresent()) {
                    list.add(stack.getCapability(capability));
                }
            }
            return list;
        }

        public <T> LazyOptional<T> getCapInSlot(int slot, Capability<T> capability) {
            return getBarrelInt().getStackInSlot(slot).getCapability(capability);
        }

        public BarrelInterfaceItemHandler getBarrelInt() {
            return blockEntity.getHandler();
        }

        @Override
        public int getSlots() {
            return totalItemSlotCount;
        }


        // returns the handler index for the slot
        protected int getIndexForSlot(int slot) {
            if (slot < 0)
                return -1;

            for (int i = 0; i < baseItemHandlerIndices.size(); i++) {
                if (slot - baseItemHandlerIndices.get(i) < 0) {
                    return i;
                }
            }
            return -1;
        }

        protected IItemHandler getHandlerFromIndex(int index) {
            if (index < 0 || index >= itemHandlers.size()) {
                return EmptyHandler.INSTANCE;
            }
            return itemHandlers.get(index).orElse(EmptyHandler.INSTANCE);
        }

        protected int getSlotFromIndex(int slot, int index) {
            if (index <= 0 || index >= baseItemHandlerIndices.size()) {
                return slot;
            }
            return slot - baseItemHandlerIndices.get(index - 1);
        }


        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            slot = getSlotFromIndex(slot, index);
            return handler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack incoming, boolean simulate) {
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            slot = getSlotFromIndex(slot, index);
            ItemStack stack = handler.insertItem(slot, incoming, simulate);
            if (!simulate && stack != incoming) {
                markDirty();
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            slot = getSlotFromIndex(slot, index);
            ItemStack stack = handler.extractItem(slot, amount, simulate);
            if (!simulate && !stack.isEmpty()) {
                markDirty();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot) {
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            int localSlot = getSlotFromIndex(slot, index);
            return handler.getSlotLimit(localSlot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            int localSlot = getSlotFromIndex(slot, index);
            return handler.isItemValid(localSlot, stack);
        }

        public void markDirty() {
            getBarrelInt().markDirty();
            getBarrelInt().clientNeedsUpdate = true;
        }
    }

    public static class BarrelInterfaceItemHandler implements SearchableItemHandler, INBTSerializable<ListTag> {

        private BarrelInterfaceBlockEntity blockEntity;

        public boolean clientNeedsUpdate;
        final List<ItemStack> barrels = new ArrayList<>();

        BarrelInterfaceItemHandler(BarrelInterfaceBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public List<Integer> getDisplaySlots(int row, String search) {
            return SearchableItemHandler.super.getDisplaySlots(row, search);
        }

        @Override
        public boolean isFull() {
            return getStoredCount() >= SIZE;
        }

        @Override
        public int getSlots() {
            return barrels.size() + 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot < barrels.size() ? barrels.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()|| !isItemValid(slot,stack)) return ItemStack.EMPTY;
            if (slot == barrels.size()) {
                //add one and move to next slot
                if (!simulate) {
                    barrels.add(ItemHandlerHelper.copyStackWithSize(stack,1));
                    blockEntity.wrapper.recomputeSlots();
                    markDirty();
                }
                return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
            } else {

            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0 || slot >= getSlots()) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = getStackInSlot(slot);

            if (!stack.isEmpty()) {

                if (!simulate) {
                    barrels.remove(slot);
                    markDirty();
                    blockEntity.wrapper.recomputeSlots();
                }
                return stack.copy();
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModItemTags.BARRELS);
        }

        public int getStoredCount() {
            return barrels.stream().mapToInt(ItemStack::getCount).sum();
        }


        @Override
        public ListTag serializeNBT() {
            ListTag nbtTagList = new ListTag();
            for (int i = 0; i < barrels.size(); i++) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                barrels.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
            return nbtTagList;
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            barrels.clear();
            for (int i = 0; i < nbt.size(); i++) {
                CompoundTag itemTags = nbt.getCompound(i);
                int slot = itemTags.getInt("Slot");

                if (slot >= 0) {
                    barrels.add(slot, ItemStack.of(itemTags));
                }
            }
        }

        public void markDirty() {
            blockEntity.setChanged();
        }
    }
}