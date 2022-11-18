package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.api.SearchableFluidHandler;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModBlockTags;
import tfar.nabba.inventory.SingleFluidSlotWrapper;
import tfar.nabba.menu.ControllerKeyFluidMenu;
import tfar.nabba.menu.ControllerKeyItemMenu;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.Utils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerBlockEntity extends BlockEntity implements HasSearchBar {
    private String search = "";

    protected final ContainerData itemDataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return barrels.get(BarrelType.BETTER).size();
                case 1:
                    return getItemHandler().getFullSlots(search);
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
                    return barrels.get(BarrelType.FLUID).size();
                case 1:
                    return getItemHandler().getFullSlots(search);
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


    private final Map<BarrelType,List<BlockPos>> barrels = new HashMap<>();
    private final Map<BarrelType,List<BlockPos>> invalid = new HashMap<>();
    private final Map<BarrelType,List<BlockPos>> pending = new HashMap<>();

    protected ControllerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        controllerHandler = new ControllerHandler(this);
        controllerFluidHandler = new ControllerFluidHandler(this);
        //need to make sure the arraylists are NOT null
        barrels.put(BarrelType.BETTER,new ArrayList<>());
        barrels.put(BarrelType.FLUID,new ArrayList<>());

        pending.put(BarrelType.BETTER,new ArrayList<>());
        pending.put(BarrelType.FLUID,new ArrayList<>());

        invalid.put(BarrelType.BETTER,new ArrayList<>());
        invalid.put(BarrelType.FLUID,new ArrayList<>());
    }

    private final ControllerHandler controllerHandler;
    private final ControllerFluidHandler controllerFluidHandler;
    protected final int[] syncSlots = new int[54];

    private static void defaultDisplaySlots(int[] ints) {
        for (int i = 0; i <  ints.length;i++) {
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


    public static ControllerBlockEntity create(BlockPos pos, BlockState state) {
        return new ControllerBlockEntity(ModBlockEntityTypes.CONTROLLER, pos, state);
    }

    public void gatherBarrels() {
        BlockPos thisPos = getBlockPos();
        List<BlockEntity> betterBarrelBlockEntities = Utils.getNearbyBarrels(level,thisPos);
        for (BlockEntity abstractBarrelBlockEntity : betterBarrelBlockEntities) {
            addBarrel(abstractBarrelBlockEntity);
        }
    }

    public void addBarrel(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        if ((!(blockEntity instanceof AbstractBarrelBlockEntity abstractBarrelBlockEntity))) {
            NABBA.LOGGER.warn("attempted to add invalid barrel {} at {}",blockEntity,pos);
            return;
        }

        BarrelType type = abstractBarrelBlockEntity.getBarrelType();

        if (invalid.get(type).contains(pos)) {
            invalid.get(type).remove(pos);
        } else {
            pending.get(type).add(pos);
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
                blockPosList.add(pos1);
            }
            barrels.put(type,blockPosList);
        }
    }

    public ControllerHandler getItemHandler() {
        return controllerHandler;
    }

    public ControllerFluidHandler getFluidHandler() {
        return controllerFluidHandler;
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
            posList.addAll(aList);
            aList.clear();
        }

        if (markDirty) {
            setChanged();
        }
    }


    public Component getDisplayName() {
        return Component.literal("controller");
    }

    @Nullable
    public AbstractContainerMenu createItemMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ControllerKeyItemMenu(pContainerId,pPlayerInventory, ContainerLevelAccess.create(level,getBlockPos()),controllerHandler, itemDataAccess,syncSlotsAccess);
    }

    @Nullable
    public AbstractContainerMenu createFluidMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ControllerKeyFluidMenu(pContainerId,pPlayerInventory, ContainerLevelAccess.create(level,getBlockPos()),controllerFluidHandler, fluidDataAccess,syncSlotsAccess);
    }

    @Override
    public void setSearchString(String search) {
        this.search = search;
    }

    @Override
    public String getSearchString() {
        return search;
    }

    public static class ControllerHandler implements SearchableItemHandler {
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
    }

    public static class ControllerFluidHandler implements SearchableFluidHandler {
        private final ControllerBlockEntity controllerBlockEntity;

        ControllerFluidHandler(ControllerBlockEntity controllerBlockEntity) {
            this.controllerBlockEntity = controllerBlockEntity;
        }

        @Override
        public int getTanks() {
            return controllerBlockEntity.barrels.get(BarrelType.FLUID).size();
        }

        //attempts to add fluid to every connected tank
        public int universalFill(FluidStack stack, FluidAction action) {
            FluidStack remainder = stack.copy();
            int totalFilled = 0;
            for (int i = 0; i < getTanks();i++) {

                if (isFluidValid(i,stack)) {
                    BlockEntity blockEntity = controllerBlockEntity.getBE(i,BarrelType.FLUID);
                    if (blockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
                        int filled = fluidBarrelBlockEntity.getFluidHandler().fill(remainder,action);
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

        public FluidStack universalDrain(int amount,FluidAction action) {
            int totalDrained = 0;
            int remaining = amount;
            Fluid fluid = null;
            for (int i = 0; i < getTanks();i++) {
                BlockEntity blockEntity = controllerBlockEntity.getBE(i,BarrelType.FLUID);
                if (blockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
                    FluidStack drained = fluidBarrelBlockEntity.getFluidHandler().drain(remaining,action);
                    if (!drained.isEmpty()) {
                        if (fluid == null) {
                            fluid = drained.getFluid();
                        }
                        int drainedAmount = drained.getAmount();
                        totalDrained += drainedAmount;
                        remaining -= drainedAmount;
                        if (remaining <= 0 && fluid != null) {
                            return new FluidStack(fluid,totalDrained);
                        }
                    }
                }
            }
            if (fluid != null) {
                return new FluidStack(fluid,totalDrained);
            }
            return FluidStack.EMPTY;
        }

        public List<Integer> getDisplaySlots(int row,String search) {
            List<Integer> disp = new ArrayList<>();
            int countForDisplay = 0;
            int index = 0;
            int startPos = 9 * row;
            while (countForDisplay < 54) {
                FluidStack stack = getFluidInTank(startPos + index);
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

        @Override
        public @NotNull FluidStack getFluidInTank(int slot) {
            if (slot >= getTanks()) return FluidStack.EMPTY;
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot,BarrelType.FLUID);
            if (blockEntity instanceof FluidBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getFluidHandler().getFluidInTank(0);
            } else {
                controllerBlockEntity.synchronize();
            }
            return FluidStack.EMPTY;
        }

        @Override
        public int fill(@NotNull FluidStack stack, FluidAction action) {
            if (stack.isEmpty()) return 0;
            return universalFill(stack, action);
        }

        @Override
        public @NotNull FluidStack drain(int amount, FluidAction action) {
            if (amount == 0) return FluidStack.EMPTY;
            return universalDrain(amount, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
                return drain(resource.getAmount(),action);
        }

        @Override
        public int getTankCapacity(int slot) {
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
        public boolean isFluidValid(int slot, @NotNull FluidStack incoming) {
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
        public int fill(int tank, int amount, FluidAction action) {
            return 0;
        }

        @Override
        public int fill(int tank, FluidStack stack, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int tank, FluidStack resource, FluidAction action) {
            FluidStack fluidStack = getFluidInTank(tank);
            if (resource.isEmpty() || !resource.isFluidEqual(fluidStack)) {
                return FluidStack.EMPTY;
            }
            return drain(tank,resource.getAmount(), action);        }

        @Override
        public @NotNull FluidStack drain(int tank, int maxDrain, FluidAction action) {
            if (tank >= getTanks())return FluidStack.EMPTY;

            BlockEntity be = controllerBlockEntity.getBE(tank,BarrelType.FLUID);

            if (be instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
               return fluidBarrelBlockEntity.getFluidHandler().drain(maxDrain,action);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public FluidActionResult attemptDrainTankWithContainer(int tank, ItemStack container, boolean b) {
            FluidActionResult result = FluidUtil.tryFillContainer(container, new SingleFluidSlotWrapper(this, tank), Integer.MAX_VALUE, null, true);


            return result;
        }
    }


    @Override
    public void setChanged() {
        super.setChanged();
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

    private LazyOptional<IItemHandler> item_optional = LazyOptional.of(this::getItemHandler);
    private LazyOptional<IFluidHandler> fluid_optional = LazyOptional.of(this::getFluidHandler);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return item_optional.cast();
        } else if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluid_optional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        item_optional.invalidate();
        fluid_optional.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        item_optional = LazyOptional.of(this::getItemHandler);
        fluid_optional = LazyOptional.of(this::getFluidHandler);
    }
}
