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
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.*;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModBlockTags;
import tfar.nabba.inventory.BetterBarrelSlotWrapper;
import tfar.nabba.inventory.FluidBarrelSlotWrapper;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.FabricUtils;
import tfar.nabba.util.NBTKeys;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerBlockEntity extends SearchableBlockEntity implements DisplayMenuProvider {
    private final Map<BarrelType,List<BlockPos>> barrels = new HashMap<>();
    private final Map<BarrelType,List<BlockPos>> invalid = new HashMap<>();
    private final Map<BarrelType,List<BlockPos>> pending = new HashMap<>();

    protected ControllerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        controllerHandler = new ControllerHandler(this);
        //need to make sure the arraylists are NOT null
        barrels.put(BarrelType.BETTER,new ArrayList<>());
        barrels.put(BarrelType.FLUID,new ArrayList<>());

        pending.put(BarrelType.BETTER,new ArrayList<>());
        pending.put(BarrelType.FLUID,new ArrayList<>());

        invalid.put(BarrelType.BETTER,new ArrayList<>());
        invalid.put(BarrelType.FLUID,new ArrayList<>());
        itemDataAccess = new ContainerData() {
            public int get(int pIndex) {
                switch (pIndex) {
                    case 0:
                        return barrels.get(BarrelType.BETTER).size();
                    case 1:
                        return getHandler().getFullItemSlots(search);
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
                        return barrels.get(BarrelType.FLUID).size();
                    case 1:
                        return getHandler().getFullItemSlots(search);
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

    private final ControllerHandler controllerHandler;


    public static ControllerBlockEntity createItem(BlockPos pos, BlockState state) {
        return new ControllerBlockEntity(ModBlockEntityTypes.CONTROLLER, pos, state);
    }

    public void gatherBarrels() {
        BlockPos thisPos = getBlockPos();
        List<BlockEntity> betterBarrelBlockEntities = FabricUtils.getNearbyFreeBarrels(level,thisPos);
        for (BlockEntity abstractBarrelBlockEntity : betterBarrelBlockEntities) {
            addBarrel(abstractBarrelBlockEntity);
        }
        synchronize();
    }

    public void addBarrel(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        if ((!(blockEntity instanceof SingleSlotBarrelBlockEntity<?> abstractBarrelBlockEntity))) {
            NABBA.LOGGER.warn("attempted to add invalid barrel {} at {}",blockEntity,pos);
            return;
        }

        BarrelType type = abstractBarrelBlockEntity.getBarrelType();

        if (invalid.get(type).contains(pos)) {
            invalid.get(type).remove(pos);
        } else {
            if (!barrels.get(type).contains(pos)) {
                pending.get(type).add(pos);
            } else {
                NABBA.LOGGER.warn("attempted to add duplicate barrel {} at {}",blockEntity,pos);
            }
        }
    }

    public void removeBarrel(BlockPos pos, BarrelType barrelType) {
        invalid.get(barrelType).add(pos);
    }

    public List<BlockPos> getBarrelsOfType(BarrelType type) {
        return barrels.get(type);
    }

    @Nullable
    public BlockEntity getBE(int i, BarrelType type) {
        if (i >= getBarrelsOfType(type).size()) {
            return null;
        }
        return getBE(getBarrelsOfType(type).get(i),type);
    }

    @Nullable
    public BlockEntity getBE(BlockPos pos,BarrelType type) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (validBarrel(blockEntity,type)) {
            return blockEntity;
        } else {
            removeBarrel(pos, type);
            return null;
        }
    }

    public boolean validBarrel(BlockPos pos,BarrelType type) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return validBarrel(blockEntity, type);
    }

    public boolean validBarrel(BlockEntity be, BarrelType type) {
        return be instanceof AbstractBarrelBlockEntity abstractBarrelBlockEntity && abstractBarrelBlockEntity.getBarrelType() == type;
    }


    public void interactWithBarrel(BlockPos pos) {

    }

    public List<BlockPos> getAllBarrels() {
        //have to be careful to avoid mutating the original list
        return barrels.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        CompoundTag barrelListNBT = new CompoundTag();

        for (BarrelType type : barrels.keySet()) {
            ListTag listTag = new ListTag();
            List<BlockPos> blockPosList = barrels.get(type);
            for (BlockPos pos : blockPosList) {
                CompoundTag tag = new CompoundTag();
                tag.putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                listTag.add(tag);
            }
            barrelListNBT.put(type.name(),listTag);
        }
        pTag.put("Barrels", barrelListNBT);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag barrelListNBT = pTag.getCompound("Barrels");
        for (BarrelType type : barrels.keySet()) {
            barrels.get(type).clear();
        }
        for (String s: barrelListNBT.getAllKeys()) {
            ListTag listTag = barrelListNBT.getList(s,Tag.TAG_COMPOUND);
            BarrelType type = BarrelType.valueOf(s);
            List<BlockPos> blockPosList = new ArrayList<>();
            for (Tag tag : listTag) {
                CompoundTag compoundTag = (CompoundTag)tag;
                int[] pos = compoundTag.getIntArray("pos");
                BlockPos pos1 = new BlockPos(pos[0],pos[1],pos[2]);
                if (!blockPosList.contains(pos1)) {
                    blockPosList.add(pos1);
                } else {
                    NABBA.LOGGER.warn("Removed duplicate {}",pos1);
                }
            }
            barrels.put(type,blockPosList);
        }
    }

    public ControllerHandler getHandler() {
        return controllerHandler;
    }

    public boolean interactWithBarrels(ItemStack stack, Player player) {
        boolean didAnything = false;
        InteractsWithBarrel interactsWithBarrel = (InteractsWithBarrel) stack.getItem();
        for (BlockPos pos : getAllBarrels()) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlockTags.BETTER_BARRELS) || state.is(ModBlockTags.FLUID_BARRELS)) {
                didAnything |= interactsWithBarrel.handleBarrel(state,stack,level,pos,player);
            } else {
            }
        }
        synchronize();
        return didAnything;
    }

    public void synchronize() {
        boolean markDirty = false;

        for (BarrelType type : invalid.keySet()) {
            List<BlockPos> rList = invalid.get(type);
            markDirty |= !rList.isEmpty();
            List<BlockPos> posList = barrels.get(type);
            rList.forEach(posList::remove);
            rList.clear();
        }

        for (BarrelType type : pending.keySet()) {
            List<BlockPos> aList = pending.get(type);
            markDirty |= !aList.isEmpty();
            List<BlockPos> posList = barrels.get(type);

            for (BlockPos pos : aList) {
                if (!posList.contains(pos)) {
                    posList.add(pos);
                }
            }
            aList.clear();
        }

        if (markDirty) {
            setChanged();
        }
    }

    @Nullable
    public AbstractContainerMenu createDisplayMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer, DisplayType type) {
        return type.createControllerMenu(pContainerId, pPlayerInventory,this);
    }



    public void storeNetworkInfo(ItemStack itemstack) {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("controller", new int[]{getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()});

        CompoundTag tag1 = new CompoundTag();

        for (BarrelType barrelType : barrels.keySet()) {
            ListTag list = new ListTag();

            List<BlockPos> blockPosList = barrels.get(barrelType);
            for (BlockPos pos : blockPosList) {
                CompoundTag tag2 = new CompoundTag();
                tag2.putIntArray("pos",getArray(pos));
                list.add(tag2);
            }
            if (!blockPosList.isEmpty())
                tag1.put(barrelType.name(),list);
        }

        ListTag list = new ListTag();
        for (BlockPos pos : findConnectedProxies()) {
            CompoundTag tag2 = new CompoundTag();
            tag2.putIntArray("pos",getArray(pos));
            list.add(tag2);
        }
        tag.put("proxies",list);

        tag.put("barrels",tag1);
        itemstack.getOrCreateTag().put(NBTKeys.NetworkInfo.name(),tag);
    }

    public List<BlockPos> findConnectedProxies() {
        return FabricUtils.getNearbyProxies(level,getBlockPos()).stream()
                .filter(blockEntity -> ((ControllerProxyBlockEntity)blockEntity).getControllerPos().equals(getBlockPos()))
                .map(BlockEntity::getBlockPos).toList();
    }

    public static int[] getArray(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static class ControllerHandler implements SearchableItemHandler,SearchableFluidHandler {
        private final ControllerBlockEntity controllerBlockEntity;

        ControllerHandler(ControllerBlockEntity controllerBlockEntity) {
            this.controllerBlockEntity = controllerBlockEntity;
        }

        @Override
        public int getSlots() {
            return controllerBlockEntity.barrels.get(BarrelType.BETTER).size();
        }


        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            if (slot >= getSlots()) return ItemStack.EMPTY;
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.BETTER);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().getStackInSlot(0);
            } else {
                controllerBlockEntity.synchronize();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || !isItemValid(slot, stack)) return stack;

            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.BETTER);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().insertItem(0,stack,simulate);
            } else {
                controllerBlockEntity.synchronize();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0|| slot >= getSlots()) return ItemStack.EMPTY;

            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.BETTER);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().extractItem(0,amount,simulate);
            } else {
                controllerBlockEntity.synchronize();
            }
            return ItemStack.EMPTY;
        }
        @Override
        public int getSlotLimit(int slot) {
            if (slot >= getSlots()) return 0;
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.BETTER);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().getSlotLimit(0);
            } else {
                controllerBlockEntity.synchronize();
            }
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack incoming) {
            if (slot < getSlots()) {
                if (controllerBlockEntity.getBE(slot,BarrelType.BETTER) instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                    return barrelBlockEntity.getItemHandler().isItemValid(0, incoming);
                } else {
                    controllerBlockEntity.synchronize();
                }
            }
            return false;
        }

        public void markDirty() {
            controllerBlockEntity.setChanged();
        }

        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public int getTanks() {
            return controllerBlockEntity.barrels.get(BarrelType.FLUID).size();
        }

        //attempts to add fluid to every connected tank
        public long universalFill(FabricFluidStack stack, FluidAction action) {
            FabricFluidStack remainder = stack.copy();
            int totalFilled = 0;
            for (int i = 0; i < getTanks();i++) {

                if (isFluidValid(i,stack)) {
                    BlockEntity blockEntity = controllerBlockEntity.getBE(i,BarrelType.FLUID);
                    if (blockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
                        long filled = fluidBarrelBlockEntity.getFluidHandler().fill(remainder,action);
                        remainder.shrink(filled);
                        totalFilled += filled;
                        if (remainder.isEmpty()) {
                            return totalFilled;
                        }
                    }
                }
            }
            return totalFilled;
        }

        public FabricFluidStack universalDrain(long amount,FluidAction action) {
            long totalDrained = 0;
            long remaining = amount;
            FluidVariant fluid = null;
            for (int i = 0; i < getTanks();i++) {
                BlockEntity blockEntity = controllerBlockEntity.getBE(i,BarrelType.FLUID);
                if (blockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
                    FabricFluidStack drained = fluidBarrelBlockEntity.getFluidHandler().drain(remaining,action);
                    if (!drained.isEmpty()) {
                        if (fluid == null) {
                            fluid = drained.getFluidVariant();
                        }
                        long drainedAmount = drained.getAmount();
                        totalDrained += drainedAmount;
                        remaining -= drainedAmount;
                        if (remaining <= 0 && fluid != null) {
                            return new FabricFluidStack(fluid,totalDrained);
                        }
                    }
                }
            }
            if (fluid != null) {
                return new FabricFluidStack(fluid,totalDrained);
            }
            return FabricFluidStack.empty();
        }

        @Override
        public @NotNull FabricFluidStack getFluidInTank(int slot) {
            if (slot >= getTanks()) return FabricFluidStack.empty();
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.FLUID);
            if (blockEntity instanceof FluidBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getFluidHandler().getFluidInTank(0);
            } else {
                controllerBlockEntity.synchronize();
            }
            return FabricFluidStack.empty();
        }

        @Override
        public long fill(@NotNull FabricFluidStack stack, IFluidHandlerShim.FluidAction action) {
            if (stack.isEmpty()) return 0;
            return universalFill(stack, action);
        }

        @Override
        public @NotNull FabricFluidStack drain(long amount, FluidAction action) {
            if (amount == 0) return FabricFluidStack.empty();
            return universalDrain(amount, action);
        }

        @Override
        public ItemStack getContainer() {
            return null;
        }

        @Override
        public @NotNull FabricFluidStack drain(FabricFluidStack resource, FluidAction action) {
            return drain(resource.getAmount(),action);
        }

        @Override
        public long getTankCapacity(int slot) {
            if (slot >= getTanks()) return 0;
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.FLUID);
            if (blockEntity instanceof FluidBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getFluidHandler().getTankCapacity(0);
            } else {
                controllerBlockEntity.synchronize();
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int slot, @NotNull FabricFluidStack incoming) {
            if (slot < getTanks()) {
                if (controllerBlockEntity.getBE(slot,BarrelType.FLUID) instanceof FluidBarrelBlockEntity barrelBlockEntity) {
                    return barrelBlockEntity.getFluidHandler().isFluidValid(0, incoming);
                } else {
                    controllerBlockEntity.synchronize();
                }
            }
            return false;
        }

        @Override
        public Storage<FluidVariant> getFluidStorage() {
            return controllerBlockEntity.getFluidStorage(null);
        }
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    //fluid api

    private CombinedStorage<FluidVariant, FluidBarrelSlotWrapper> fluidStorage;

    public CombinedStorage<FluidVariant, FluidBarrelSlotWrapper> getFluidStorage(Direction direction) {

        int tanks = controllerHandler.getTanks();

        if (fluidStorage != null && fluidStorage.parts.size() != tanks) {
            fluidStorage = null;
        }
        if (fluidStorage == null) {
            fluidStorage = createFluid();
        }
        return fluidStorage;
    }


    public CombinedStorage<FluidVariant, FluidBarrelSlotWrapper> createFluid() {
        int slots = controllerHandler.getTanks();

        List<FluidBarrelSlotWrapper> storages = new ArrayList<>();

        for (int i = 0 ;i < slots;i++) {
            BlockEntity blockEntity = getBE(i,BarrelType.FLUID);
            if (blockEntity instanceof FluidBarrelBlockEntity barrelBlockEntity) {
                FluidBarrelSlotWrapper wrapper = barrelBlockEntity.getFluidStorage();
                storages.add(wrapper);
            }
        }
        return new CombinedStorage<>(storages);
    }

    //item api

    private CombinedStorage<ItemVariant,BetterBarrelSlotWrapper> itemStorage;

    public CombinedStorage<ItemVariant,BetterBarrelSlotWrapper> getItemStorage(Direction direction) {

        if (itemStorage != null && itemStorage.parts.size() != controllerHandler.getSlots()) {
            itemStorage = null;
        }
        if (itemStorage == null) {
            itemStorage = createItem();
        }
        return itemStorage;
    }


    public CombinedStorage<ItemVariant, BetterBarrelSlotWrapper> createItem() {
        int slots = controllerHandler.getSlots();

        List<BetterBarrelSlotWrapper> storages = new ArrayList<>();

        for (int i = 0 ;i < slots;i++) {
            BlockEntity blockEntity = getBE(i,BarrelType.BETTER);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                BetterBarrelSlotWrapper wrapper = barrelBlockEntity.getStorage(null);
                storages.add(wrapper);
            }
        }
        return new CombinedStorage<>(storages);
    }
}
