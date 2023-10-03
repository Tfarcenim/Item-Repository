package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBAFabric;
import tfar.nabba.api.*;
import tfar.nabba.capability.AntiBarrelItemStackItemHandler;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModItemTags;
import tfar.nabba.menu.BarrelInterfaceMenu;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.shim.IItemHandlerShim;
import tfar.nabba.util.CommonUtils;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.ItemStackWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BarrelInterfaceBlockEntity extends SearchableBlockEntity implements MenuProvider, DisplayMenuProvider {

    private final BarrelInterfaceItemHandler handler = new BarrelInterfaceItemHandler(this);
    private final BarrelWrapper wrapper = new BarrelWrapper(this);

    public BarrelInterfaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(ModBlockEntityTypes.BARREL_INTERFACE, pPos, pBlockState);
    }

    public BarrelInterfaceBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        itemDataAccess = new ContainerData() {
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
                if (pIndex == 0) {
                }
            }

            public int getCount() {
                return 2;
            }
        };

        fluidDataAccess = new ContainerData() {
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
                if (pIndex == 0) {
                }
            }
            public int getCount() {
                return 2;
            }
        };
    }

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
            if (pIndex == 0) {
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public BarrelInterfaceItemHandler getInventory() {
        return handler;
    }

    public BarrelWrapper getWrapper() {
        return wrapper;
    }


    private static void defaultDisplaySlots(int[] ints) {
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
    }

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
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Items", getInventory().serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        getInventory().deserializeNBT(pTag.getList("Items", Tag.TAG_COMPOUND));
    }

    public static class BarrelWrapper implements SearchableItemHandler, SearchableFluidHandler {

        private final BarrelInterfaceBlockEntity blockEntity;

        protected List<Integer> baseItemHandlerIndices = new ArrayList<>();
        protected List<Integer> baseFluidHandlerIndices = new ArrayList<>();
        protected int totalItemSlotCount;
        protected int totalFluidSlotCount;
        protected List<IItemHandlerShim> itemHandlers; // the handlers
        protected List<IFluidHandlerShim> fluidHandlers; // the handlers

        public BarrelWrapper(BarrelInterfaceBlockEntity blockEntity) {
            this.blockEntity = blockEntity;

        }

        //prevent voiding
        @Override
        public ItemStack storeItem(ItemStack stack, boolean simulate) {
            if (!blockEntity.isRemoved()) {
                return SearchableItemHandler.super.storeItem(stack, simulate);
            }
            return stack;
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
                index += itemHandlers.get(i).getSlots();
                baseItemHandlerIndices.add(index);
            }
            this.totalItemSlotCount = index;
        }

        public void recomputeFluidSlots() {
            fluidHandlers = fluidCaps();
            baseFluidHandlerIndices.clear();
            int index = 0;
            for (int i = 0; i < fluidHandlers.size(); i++) {
                index += fluidHandlers.get(i).getTanks();
                baseFluidHandlerIndices.add(index);
            }
            this.totalFluidSlotCount = index;
        }

        public List<IItemHandlerShim> itemCaps() {
            return null;//caps(ForgeCapabilities.ITEM_HANDLER);
        }

        public List<IFluidHandlerShim> fluidCaps() {
            return null;//caps(ForgeCapabilities.FLUID_HANDLER_ITEM);
        }

      /*  public <T> List<LazyOptional<T>> caps(Capability<T> capability) {
            List<LazyOptional<T>> list = new ArrayList<>();
            for (ItemStack stack : getBarrelInt().barrels) {



                if (stack.getCapability(capability).isPresent()) {
                    list.add(stack.getCapability(capability));
                }
            }
            return list;
        }*/

        public BarrelInterfaceItemHandler getBarrelInt() {
            return blockEntity.getInventory();
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

        protected IItemHandlerShim getItemHandlerFromIndex(int index) {
            if (index < 0 || index >= itemHandlers.size()) {
                return null;
            }
            return itemHandlers.get(index);
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

        protected IFluidHandlerShim getFluidHandlerFromIndex(int index) {
            if (index < 0 || index >= fluidHandlers.size()) {
                return null;
            }
            return fluidHandlers.get(index);
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
            IItemHandlerShim handler = getItemHandlerFromIndex(index);
            slot = getItemSlotFromIndex(slot, index);
            return handler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack incoming, boolean simulate) {
            int index = getIndexForItemSlot(slot);
            IItemHandlerShim handler = getItemHandlerFromIndex(index);

            //probably a safe cast
            IItemHandlerItem iItemHandlerItem = (IItemHandlerItem)handler;
            ItemStack container = iItemHandlerItem.getContainer();
            if (container.getCount() > 1) {
                //need to split and add remainder
                ItemStack leftover = CommonUtils.copyStackWithSize(container,container.getCount() - 1);
                container.setCount(1);
                blockEntity.handler.insertItem(blockEntity.handler.getSlots(),leftover,false);
            }

            slot = getItemSlotFromIndex(slot, index);
            ItemStack stack = handler.insertItem(slot, incoming, simulate);
            if (!simulate && stack != incoming) {
                markDirty();
                if (handler instanceof AntiBarrelItemStackItemHandler) {
                    recomputeItemSlots();
                }
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int index = getIndexForItemSlot(slot);
            IItemHandlerShim handler = getItemHandlerFromIndex(index);
            slot = getItemSlotFromIndex(slot, index);


            //probably a safe cast
            IItemHandlerItem iItemHandlerItem = (IItemHandlerItem)handler;
            ItemStack container = iItemHandlerItem.getContainer();
            if (container.getCount() > 1) {
                //need to split and add remainder
                ItemStack leftover = CommonUtils.copyStackWithSize(container,container.getCount() - 1);
                container.setCount(1);
                blockEntity.handler.insertItem(blockEntity.handler.getSlots(),leftover,false);
            }

            ItemStack stack = handler.extractItem(slot, amount, simulate);
            if (!simulate && !stack.isEmpty()) {
                markDirty();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot) {
            int index = getIndexForItemSlot(slot);
            IItemHandlerShim handler = getItemHandlerFromIndex(index);
            int localSlot = getItemSlotFromIndex(slot, index);
            return handler.getSlotLimit(localSlot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            int index = getIndexForItemSlot(slot);
            IItemHandlerShim handler = getItemHandlerFromIndex(index);
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
        public @NotNull FabricFluidStack getFluidInTank(int tank) {
            int index = getIndexForFluidSlot(tank);
            IFluidHandlerShim handler = getFluidHandlerFromIndex(index);
            tank = getFluidSlotFromIndex(tank, index);
            return handler.getFluidInTank(tank);
        }

        @Override
        public long getTankCapacity(int tank) {
            int index = getIndexForFluidSlot(tank);
            IFluidHandlerShim handler = getFluidHandlerFromIndex(index);
            int localSlot = getFluidSlotFromIndex(tank, index);
            return handler.getTankCapacity(localSlot);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FabricFluidStack stack) {
            int index = getIndexForFluidSlot(tank);
            IFluidHandlerShim handler = getFluidHandlerFromIndex(index);
            int localSlot = getFluidSlotFromIndex(tank, index);
            return handler.isFluidValid(localSlot, stack);
        }

        @Override
        public long fill(FabricFluidStack resource, FluidAction action) {
            int filled = 0;
            FabricFluidStack remaining = resource.copy();
            long fill = 0;
            for (IFluidHandlerShim handlerLazyOptional : fluidHandlers) {
                    ItemStack container = handlerLazyOptional.getContainer();
                    if (container.getCount() > 1) {
                            //need to split and add remainder
                            ItemStack leftover = CommonUtils.copyStackWithSize(container,container.getCount() - 1);
                            container.setCount(1);
                            blockEntity.handler.insertItem(blockEntity.handler.getSlots(),leftover,false);
                    }
                    return handlerLazyOptional.fill(remaining, action);
                }
                if (fill > 0) {
                    remaining.shrink(fill);
                    filled += fill;
                }
            return filled;
        }

        @Override
        public @NotNull FabricFluidStack drain(FabricFluidStack resource, FluidAction action) {

            FabricFluidStack drained =  FabricFluidStack.empty();
            FabricFluidStack remaining = resource.copy();
            for (IFluidHandlerShim fluidHandler : fluidHandlers) {
                FabricFluidStack drain = null;//fluidHandler.map(fl -> {

                    ItemStack container = fluidHandler.getContainer();
                    if (container.getCount() > 1) {
                        //need to split and add remainder
                        ItemStack leftover = CommonUtils.copyStackWithSize(container,container.getCount() - 1);
                        container.setCount(1);
                        blockEntity.handler.insertItem(blockEntity.handler.getSlots(),leftover,false);
                    }

                   // return fl.drain(remaining, action);

                if (!drain.isEmpty()) {
                    if (drained.isEmpty()) {
                        drained = drain;
                        remaining.shrink(drain.getAmount());
                    } else {
                        if (drain.sameFluid(drained)) {
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
        public @NotNull FabricFluidStack drain(long maxDrain, FluidAction action) {

            FabricFluidStack drained = FabricFluidStack.empty();
            final long[] remaining = new long[1];
            remaining[0] = maxDrain;

            FabricFluidStack drain = FabricFluidStack.empty();//todo

            for (IFluidHandlerShim handlerLazyOptional : fluidHandlers) {

                ItemStack container = handlerLazyOptional.getContainer();
                if (container.getCount() > 1) {
                    //need to split and add remainder
                    ItemStack leftover = CommonUtils.copyStackWithSize(container, container.getCount() - 1);
                    container.setCount(1);
                    blockEntity.handler.insertItem(blockEntity.handler.getSlots(), leftover, false);
                }

                return FabricFluidStack.empty();//fl.drain(remaining[0], action);
            }
                if (!drain.isEmpty()) {
                    if (drained.isEmpty()) {
                        drained = drain;
                        remaining[0] -= drain.getAmount();
                    } else {
                        if (drain.getFluidVariant().equals(drained.getFluidVariant())) {
                            drained.grow(drain.getAmount());
                            remaining[0] -= drain.getAmount();
                        } else {

                        }
                    }
                }
                return null;
            }

        @Override
        public ItemStack getContainer() {
            return null;
        }

        @Override
        public boolean isFull() {
            return false;
        }

    }

    public static class BarrelInterfaceItemHandler implements SearchableItemHandler{

        private final BarrelInterfaceBlockEntity blockEntity;

        final List<ItemStack> barrels = new ArrayList<>();

        BarrelInterfaceItemHandler(BarrelInterfaceBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        public void sort() {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack stack : barrels) {
                if (!stack.isEmpty()) {
                    CommonUtils.merge(stacks, stack.copy());
                }
            }

            List<ItemStackWrapper> wrappers = CommonUtils.wrap(stacks);

            Collections.sort(wrappers);

            barrels.clear();

            //split up the stacks and add them to the slot

            wrappers.forEach(itemStackWrapper -> barrels.add(itemStackWrapper.stack));
        }

        @Override
        public boolean isFull() {
            return getStoredCount() >= NABBAFabric.ServerCfg.barrel_interface_storage;
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
            if (stack.isEmpty() || !isItemValid(slot, stack)) return stack;
            if (slot >= barrels.size()) {
                //add and move to next slot
                if (!simulate) {
                    barrels.add(stack.copy());
                    blockEntity.wrapper.recomputeSlots();
                    markDirty();
                }
                return ItemStack.EMPTY;
            } else {
                ItemStack existing = barrels.get(slot);
                int limit = getSlotLimit(slot);

                if (!existing.isEmpty()) {
                    if (!CommonUtils.canItemStacksStack(stack, existing))
                        return stack;

                    limit -= existing.getCount();
                }

                if (limit <= 0)
                    return stack;

                boolean reachedLimit = stack.getCount() > limit;

                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.barrels.set(slot, reachedLimit ? CommonUtils.copyStackWithSize(stack, limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }
                    markDirty();
                }
                return reachedLimit ? CommonUtils.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0 || slot >= getSlots()) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = getStackInSlot(slot);

            if (!stack.isEmpty()) {

                if (amount >= stack.getCount()) {
                    if (!simulate) {
                        barrels.remove(slot);
                        markDirty();
                        blockEntity.wrapper.recomputeSlots();
                    }
                    return stack.copy();
                } else {
                    if (!simulate) {
                        barrels.get(slot).shrink(amount);
                        markDirty();
                        blockEntity.wrapper.recomputeSlots();
                    }
                    return CommonUtils.copyStackWithSize(stack,amount);
                }
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModItemTags.BARRELS) && !isFull();
        }

        public int getStoredCount() {
            return barrels.stream().mapToInt(ItemStack::getCount).sum();
        }


    //    @Override
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

       // @Override
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

        public List<ItemStack> getBarrels() {
            return barrels;
        }
    }

    @Nullable
    public AbstractContainerMenu createDisplayMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer, DisplayType type) {
        return type.createBarrelInterfaceMenu(pContainerId, pPlayerInventory,this);
    }
}