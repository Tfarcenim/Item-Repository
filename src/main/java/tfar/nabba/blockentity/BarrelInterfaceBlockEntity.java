package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.*;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModItemTags;
import tfar.nabba.menu.BarrelInterfaceMenu;
import tfar.nabba.menu.ControllerKeyFluidMenu;
import tfar.nabba.menu.ControllerKeyItemMenu;

import java.util.ArrayList;
import java.util.List;

public class BarrelInterfaceBlockEntity extends BlockEntity implements MenuProvider, HasSearchBar, ItemMenuProvider, FluidMenuProvider {

    private BarrelInterfaceItemHandler handler = new BarrelInterfaceItemHandler(this);
    private BarrelWrapper wrapper = new BarrelWrapper(this);

    private String search = "";

    protected final ContainerData itemDataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return getWrapper().totalItemSlotCount;
                case 1:
                    return 1;//getWrapper().getFullSlots(search);
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

    protected final ContainerData fluidDataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return getWrapper().totalFluidSlotCount;
                case 1:
                    return 1;//getItemHandler().getFullSlots(search);
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
                    return getHandler().getFullItemSlots(search);
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

    public BarrelWrapper getWrapper() {
        return wrapper;
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
        return cap == ForgeCapabilities.ITEM_HANDLER || cap == ForgeCapabilities.FLUID_HANDLER ? LazyOptional.of(() -> wrapper).cast() : super.getCapability(cap, side);
    }

    public static final int SIZE = 256;

    public static class BarrelWrapper implements SearchableItemHandler, SearchableFluidHandler {

        private BarrelInterfaceBlockEntity blockEntity;

        protected List<Integer> baseItemHandlerIndices = new ArrayList<>();
        protected List<Integer> baseFluidHandlerIndices = new ArrayList<>();
        protected int totalItemSlotCount;
        protected int totalFluidSlotCount;
        protected List<LazyOptional<IItemHandler>> itemHandlers; // the handlers
        protected List<LazyOptional<IFluidHandler>> fluidHandlers; // the handlers

        public BarrelWrapper(BarrelInterfaceBlockEntity blockEntity) {
            this.blockEntity = blockEntity;

        }

        public void recomputeSlots() {
            recomputeItemSlots();
            recomputeFluidSlots();
        }

        public void recomputeItemSlots() {
            itemHandlers = itemCaps();
            baseItemHandlerIndices.clear();
            int index = 0;
            for (int i = 0; i < itemHandlers.size(); i++) {
                index += itemHandlers.get(i).map(IItemHandler::getSlots).orElse(0);
                baseItemHandlerIndices.add(index);
            }
            this.totalItemSlotCount = index;
        }

        public void recomputeFluidSlots() {
            fluidHandlers = fluidCaps();
            baseFluidHandlerIndices.clear();
            int index = 0;
            for (int i = 0; i < fluidHandlers.size(); i++) {
                index += fluidHandlers.get(i).map(IFluidHandler::getTanks).orElse(0);
                baseFluidHandlerIndices.add(index);
            }
            this.totalFluidSlotCount = index;
        }

        public List<LazyOptional<IItemHandler>> itemCaps() {
            return caps(ForgeCapabilities.ITEM_HANDLER);
        }

        public List<LazyOptional<IFluidHandler>> fluidCaps() {
            return caps(ForgeCapabilities.FLUID_HANDLER);
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
        protected int getIndexForItemSlot(int slot) {
            if (slot < 0)
                return -1;

            for (int i = 0; i < baseItemHandlerIndices.size(); i++) {
                if (slot - baseItemHandlerIndices.get(i) < 0) {
                    return i;
                }
            }
            return -1;
        }

        protected IItemHandler getItemHandlerFromIndex(int index) {
            if (index < 0 || index >= itemHandlers.size()) {
                return EmptyHandler.INSTANCE;
            }
            return itemHandlers.get(index).orElse(EmptyHandler.INSTANCE);
        }

        protected int getItemSlotFromIndex(int slot, int index) {
            if (index <= 0 || index >= baseItemHandlerIndices.size()) {
                return slot;
            }
            return slot - baseItemHandlerIndices.get(index - 1);
        }


        // returns the handler index for the slot
        protected int getIndexForFluidSlot(int slot) {
            if (slot < 0)
                return -1;

            for (int i = 0; i < baseFluidHandlerIndices.size(); i++) {
                if (slot - baseFluidHandlerIndices.get(i) < 0) {
                    return i;
                }
            }
            return -1;
        }

        protected IFluidHandler getFluidHandlerFromIndex(int index) {
            if (index < 0 || index >= fluidHandlers.size()) {
                return EmptyFluidHandler.INSTANCE;
            }
            return fluidHandlers.get(index).orElse(EmptyFluidHandler.INSTANCE);
        }

        protected int getFluidSlotFromIndex(int slot, int index) {
            if (index <= 0 || index >= baseFluidHandlerIndices.size()) {
                return slot;
            }
            return slot - baseFluidHandlerIndices.get(index - 1);
        }


        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            int index = getIndexForItemSlot(slot);
            IItemHandler handler = getItemHandlerFromIndex(index);
            slot = getItemSlotFromIndex(slot, index);
            return handler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack incoming, boolean simulate) {
            int index = getIndexForItemSlot(slot);
            IItemHandler handler = getItemHandlerFromIndex(index);
            slot = getItemSlotFromIndex(slot, index);
            ItemStack stack = handler.insertItem(slot, incoming, simulate);
            if (!simulate && stack != incoming) {
                markDirty();
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int index = getIndexForItemSlot(slot);
            IItemHandler handler = getItemHandlerFromIndex(index);
            slot = getItemSlotFromIndex(slot, index);
            ItemStack stack = handler.extractItem(slot, amount, simulate);
            if (!simulate && !stack.isEmpty()) {
                markDirty();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot) {
            int index = getIndexForItemSlot(slot);
            IItemHandler handler = getItemHandlerFromIndex(index);
            int localSlot = getItemSlotFromIndex(slot, index);
            return handler.getSlotLimit(localSlot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            int index = getIndexForItemSlot(slot);
            IItemHandler handler = getItemHandlerFromIndex(index);
            int localSlot = getItemSlotFromIndex(slot, index);
            return handler.isItemValid(localSlot, stack);
        }

        public void markDirty() {
            getBarrelInt().markDirty();
        }

        @Override
        public int getTanks() {
            return totalFluidSlotCount;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            int index = getIndexForFluidSlot(tank);
            IFluidHandler handler = getFluidHandlerFromIndex(index);
            tank = getFluidSlotFromIndex(tank, index);
            return handler.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            int index = getIndexForFluidSlot(tank);
            IFluidHandler handler = getFluidHandlerFromIndex(index);
            int localSlot = getFluidSlotFromIndex(tank, index);
            return handler.getTankCapacity(localSlot);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            int index = getIndexForFluidSlot(tank);
            IFluidHandler handler = getFluidHandlerFromIndex(index);
            int localSlot = getFluidSlotFromIndex(tank, index);
            return handler.isFluidValid(localSlot, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int filled = 0;
            FluidStack remaining = resource.copy();
            for (LazyOptional<IFluidHandler> handlerLazyOptional : fluidHandlers) {
                int fill = handlerLazyOptional.map(fl -> fl.fill(remaining, action)).orElse(0);
                if (fill > 0) {
                    remaining.shrink(fill);
                    filled += fill;
                }
            }
            return filled;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {

            FluidStack drained = FluidStack.EMPTY;
            FluidStack remaining = resource.copy();
            for (LazyOptional<IFluidHandler> handlerLazyOptional : fluidHandlers) {
                FluidStack drain = handlerLazyOptional.map(fl -> fl.drain(remaining, action)).orElse(FluidStack.EMPTY);

                if (!drain.isEmpty()) {
                    if (drained.isEmpty()) {
                        drained = drain;
                        remaining.shrink(drain.getAmount());
                    } else {
                        if (drain.isFluidEqual(drained)) {
                            drained.grow(drain.getAmount());
                            remaining.shrink(drain.getAmount());
                        } else {

                        }
                    }
                }
            }

            return drained;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {

            FluidStack drained = FluidStack.EMPTY;
            final int[] remaining = new int[1];
            remaining[0] = maxDrain;
            for (LazyOptional<IFluidHandler> handlerLazyOptional : fluidHandlers) {
                FluidStack drain = handlerLazyOptional.map(fl -> fl.drain(remaining[0], action)).orElse(FluidStack.EMPTY);

                if (!drain.isEmpty()) {
                    if (drained.isEmpty()) {
                        drained = drain;
                        remaining[0] -=drain.getAmount();
                    } else {
                        if (drain.isFluidEqual(drained)) {
                            drained.grow(drain.getAmount());
                            remaining[0] -=drain.getAmount();
                        } else {

                        }
                    }
                }
            }
            return drained;
        }

        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public int fill(int tank, int amount, FluidAction action) {
            return 0;
        }

        @Override
        public int fill(int tank, FluidStack stack, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int tank, FluidStack resource, FluidAction action) {
            return null;
        }

        @Override
        public @NotNull FluidStack drain(int tank, int maxDrain, FluidAction action) {
            return null;
        }
    }

    public static class BarrelInterfaceItemHandler implements SearchableItemHandler, INBTSerializable<ListTag> {

        private BarrelInterfaceBlockEntity blockEntity;

        final List<ItemStack> barrels = new ArrayList<>();

        BarrelInterfaceItemHandler(BarrelInterfaceBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
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

    @Nullable
    public AbstractContainerMenu createItemMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ControllerKeyItemMenu(pContainerId,pPlayerInventory, ContainerLevelAccess.create(level,getBlockPos()),wrapper, itemDataAccess,syncSlotsAccess);
    }

    @Nullable
    public AbstractContainerMenu createFluidMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ControllerKeyFluidMenu(pContainerId,pPlayerInventory, ContainerLevelAccess.create(level,getBlockPos()),wrapper, fluidDataAccess,syncSlotsAccess);
    }

}