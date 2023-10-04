package tfar.nabba.blockentity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBAFabric;
import tfar.nabba.api.*;
import tfar.nabba.capability.BetterBarrelItemStackItemHandler;
import tfar.nabba.capability.FluidBarrelItemStackItemHandler;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModItemTags;
import tfar.nabba.inventory.BetterBarrelSlotWrapper;
import tfar.nabba.inventory.FluidBarrelSlotWrapper;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.item.barrels.FluidBarrelBlockItem;
import tfar.nabba.menu.BarrelInterfaceMenu;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.shim.IItemHandlerShim;
import tfar.nabba.util.*;

import java.util.*;

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

        private final Map<BarrelType,List<Integer>> slot_cache = new HashMap<>();

        protected int totalItemSlotCount;
        protected int totalFluidSlotCount;
        protected List<IItemHandlerShim> itemHandlers; // the handlers
      //  protected List<IFluidHandlerShim> fluidHandlers; // the handlers

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

        @Override
        public Storage<FluidVariant> getFluidStorage() {
            return null;
        }

        public void recomputeSlots() {
            slot_cache.clear();

            slot_cache.put(BarrelType.BETTER,new ArrayList<>());
            slot_cache.put(BarrelType.FLUID,new ArrayList<>());

            List<ItemStack> barrels = getBarrelInt().barrels;
            for (int i = 0; i < barrels.size(); i++) {
                ItemStack stack = barrels.get(i);
                if (stack.getItem() instanceof BetterBarrelBlockItem) {
                    slot_cache.get(BarrelType.BETTER).add(i);
                } else if (stack.getItem() instanceof FluidBarrelBlockItem) {
                    slot_cache.get(BarrelType.FLUID).add(i);
                }
            }
        }

        public BarrelInterfaceItemHandler getBarrelInt() {
            return blockEntity.getInventory();
        }

        @Override
        public int getSlots() {
            return totalItemSlotCount;
        }


        protected int mapSlotToItemBarrelIndex(int slot) {
            if (slot < 0)
                return -1;
            if (slot < slot_cache.get(BarrelType.BETTER).size()) {
                return slot_cache.get(BarrelType.BETTER).get(slot);
            }
            return -1;
        }


        // returns the handler index for the slot
        protected int mapSlotToFluidBarrelSlot(int slot) {
            if (slot < 0)
                return -1;
            if (slot < slot_cache.get(BarrelType.FLUID).size()) {
                return slot_cache.get(BarrelType.FLUID).get(slot);
            }
            return -1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            int index = mapSlotToItemBarrelIndex(slot);
            if (index == -1) return ItemStack.EMPTY;
            ItemStack barrel = getBarrelInt().barrels.get(index);
            return BlockItemBarrelUtils.getStoredItem(barrel);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack incoming, boolean simulate) {
            int index = mapSlotToItemBarrelIndex(slot);
            if (index == -1) return incoming;
            IItemHandlerShim iItemHandlerShim = new BetterBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            ItemStack stack = iItemHandlerShim.insertItem(slot, incoming, simulate);
            if (!simulate && stack != incoming) {
                markDirty();
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int index = mapSlotToItemBarrelIndex(slot);
            if (index == -1) return ItemStack.EMPTY;
            IItemHandlerShim iItemHandlerShim = new BetterBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            ItemStack stack = iItemHandlerShim.extractItem(slot, amount, simulate);
            if (!simulate && !stack.isEmpty()) {
                markDirty();
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot) {
            int index = mapSlotToItemBarrelIndex(slot);
            if (index == -1) return 0;
            IItemHandlerShim iItemHandlerShim = new BetterBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            return iItemHandlerShim.getSlotLimit(0);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            int index = mapSlotToItemBarrelIndex(slot);
            if (index == -1) return false;
            IItemHandlerShim iItemHandlerShim = new BetterBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            return iItemHandlerShim.isItemValid(0, stack);
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
            int index = mapSlotToFluidBarrelSlot(tank);
            IFluidHandlerShim handler = new FluidBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            return handler.getFluidInTank(0);
        }

        @Override
        public long getTankCapacity(int tank) {
            int index = mapSlotToFluidBarrelSlot(tank);
            IFluidHandlerShim handler = new FluidBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            return handler.getTankCapacity(0);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FabricFluidStack stack) {
            int index = mapSlotToFluidBarrelSlot(tank);
            IFluidHandlerShim handler = new FluidBarrelItemStackItemHandler(getBarrelInt().barrels.get(index));
            return handler.isFluidValid(0, stack);
        }

        @Override
        public long fill(FabricFluidStack resource, FluidAction action) {
            int filled = 0;
            FabricFluidStack remaining = resource.copy();
            List<Integer> fluidBarrelSlots = slot_cache.get(BarrelType.FLUID);
            for (int i = 0; i < fluidBarrelSlots.size();i++) {
                int index = fluidBarrelSlots.get(i);
                ItemStack barrel = getBarrelInt().barrels.get(index);
                FluidBarrelItemStackItemHandler fluidBarrelItemStackItemHandler = new FluidBarrelItemStackItemHandler(barrel);
                long amount = fluidBarrelItemStackItemHandler.fill(remaining,FluidAction.EXECUTE);
                remaining.shrink(amount);
                filled+= amount;
                if (remaining.isEmpty()) break;
            }
            return filled;
        }

        @Override
        public @NotNull FabricFluidStack drain(FabricFluidStack resource, FluidAction action) {

            FabricFluidStack drained = FabricFluidStack.empty();
            FabricFluidStack remaining = resource.copy();
            List<Integer> fluidBarrelSlots = slot_cache.get(BarrelType.FLUID);
            for (int i = 0; i < fluidBarrelSlots.size();i++) {
                int index = fluidBarrelSlots.get(i);
                ItemStack barrel = getBarrelInt().barrels.get(index);
                FluidBarrelItemStackItemHandler fluidBarrelItemStackItemHandler = new FluidBarrelItemStackItemHandler(barrel);
                FabricFluidStack drain = fluidBarrelItemStackItemHandler.drain(remaining,FluidAction.SIMULATE);
                if (remaining.sameFluid(drain)) {
                    if (drained.isEmpty()) {
                        drained = drain;
                    } else {
                        drained.grow(drain.getAmount());
                    }
                    remaining.shrink(drain.getAmount());
                } else {
                    continue;
                }
                if (remaining.isEmpty()) break;
            }
            return drained;
        }

        @Override
        public @NotNull FabricFluidStack drain(long maxDrain, FluidAction action) {
            FabricFluidStack drained = FabricFluidStack.empty();
            long remaining = maxDrain;
            List<Integer> fluidBarrelSlots = slot_cache.get(BarrelType.FLUID);
            for (int i = 0; i < fluidBarrelSlots.size();i++) {
                int index = fluidBarrelSlots.get(i);
                ItemStack barrel = getBarrelInt().barrels.get(index);
                FluidBarrelItemStackItemHandler fluidBarrelItemStackItemHandler = new FluidBarrelItemStackItemHandler(barrel);
                FabricFluidStack drain = fluidBarrelItemStackItemHandler.drain(remaining,FluidAction.SIMULATE);
                if (drained.isEmpty()) {
                    drained = drain;
                } else {
                    drained.grow(drain.getAmount());
                }
                remaining -= drain.getAmount();
                if (remaining <= 0) break;
            }
            return drained;
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
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return (stack.is(ModItemTags.BETTER_BARRELS) || stack.is(ModItemTags.FLUID_BARRELS)) && !isFull();
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

    //fluid api

    private CombinedStorage<FluidVariant, FluidBarrelBlockItem.SingleItemBarrelWrapper> fluidStorage;

    public CombinedStorage<FluidVariant, FluidBarrelBlockItem.SingleItemBarrelWrapper> getFluidStorage(Direction direction) {

        int tanks = wrapper.getTanks();

        if (fluidStorage != null && fluidStorage.parts.size() != tanks) {
            fluidStorage = null;
        }
        if (fluidStorage == null) {
            fluidStorage = createFluid();
        }
        return fluidStorage;
    }


    public CombinedStorage<FluidVariant, FluidBarrelBlockItem.SingleItemBarrelWrapper> createFluid() {
        List<Integer> fluidBarrelSlots = wrapper.slot_cache.get(BarrelType.FLUID);
        List<FluidBarrelBlockItem.SingleItemBarrelWrapper> storages = new ArrayList<>();
        for (int i = 0 ;i < fluidBarrelSlots.size();i++) {
            int index = fluidBarrelSlots.get(i);
            ItemStack barrel = handler.barrels.get(index);
            FluidBarrelBlockItem.SingleItemBarrelWrapper singleItemBarrelWrapper = FluidBarrelBlockItem.getStorage(barrel);
            storages.add(singleItemBarrelWrapper);
        }
        return new CombinedStorage<>(storages);
    }

    //item api

    private CombinedStorage<ItemVariant, BetterBarrelBlockItem.SingleItemBarrelWrapper> itemStorage;

    public CombinedStorage<ItemVariant, BetterBarrelBlockItem.SingleItemBarrelWrapper> getItemStorage(Direction direction) {
        int slots = wrapper.getSlots();

        if (itemStorage != null && itemStorage.parts.size() != slots) {
            itemStorage = null;
        }
        if (itemStorage == null) {
            itemStorage = createItem();
        }
        return itemStorage;
    }


    public CombinedStorage<ItemVariant, BetterBarrelBlockItem.SingleItemBarrelWrapper> createItem() {
        List<Integer> betterBarrelSlots = wrapper.slot_cache.get(BarrelType.BETTER);
        List<BetterBarrelBlockItem.SingleItemBarrelWrapper> storages = new ArrayList<>();
        for (int i = 0 ;i < betterBarrelSlots.size();i++) {
            int index = betterBarrelSlots.get(i);
            ItemStack barrel = handler.barrels.get(index);
            BetterBarrelBlockItem.SingleItemBarrelWrapper singleItemBarrelWrapper = BetterBarrelBlockItem.getStorage(barrel);
            storages.add(singleItemBarrelWrapper);
        }
        return new CombinedStorage<>(storages);
    }

    @Nullable
    public AbstractContainerMenu createDisplayMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer, DisplayType type) {
        return type.createBarrelInterfaceMenu(pContainerId, pPlayerInventory,this);
    }
}