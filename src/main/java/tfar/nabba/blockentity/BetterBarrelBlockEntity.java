package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.api.Upgrade;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Upgrades;
import tfar.nabba.util.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BetterBarrelBlockEntity extends BlockEntity {

    private List<UpgradeStack> upgrades = new ArrayList<>();
    private int color = 0xff99ff;
    private double size = .5;
    private ItemStack ghost = ItemStack.EMPTY;
    private transient int cachedStorage = Utils.INVALID;
    private transient int cachedUsedUpgradeSlots = Utils.INVALID;
    private BlockPos controllerPos;

    protected BetterBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new BarrelHandler(this);
    }

    private final BarrelHandler barrelHandler;

    public static BetterBarrelBlockEntity create(BlockPos pos, BlockState state) {
        return new BetterBarrelBlockEntity(ModBlockEntityTypes.BETTER_BARREL, pos, state);
    }

    public static BetterBarrelBlockEntity createDiscrete(BlockPos pos, BlockState state) {
        return new BetterBarrelBlockEntity(ModBlockEntityTypes.BETTER_BARREL, pos, state);
    }

    public int getStorage() {
        if (cachedStorage == -1) {//save CPU cycles by not iterating the upgrade map
            cachedStorage = computeStorage();
        }
        return cachedStorage;
    }

    public int getUsedSlots() {
        if (cachedUsedUpgradeSlots == -1) {//save CPU cycles by not iterating the upgrade map
            cachedUsedUpgradeSlots = computeUsedUpgradeSlots();
        }
        return cachedUsedUpgradeSlots;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;

        if (controllerPos != null) {
            BlockEntity blockEntity = level.getBlockEntity(getControllerPos());
            if (blockEntity instanceof ControllerBlockEntity controller) {
                controller.addBarrel(getBlockPos());
            }
        }
        setChanged();
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void removeController() {
        if (controllerPos != null) {

            BlockEntity blockEntity = level.getBlockEntity(getControllerPos());
            if (blockEntity instanceof ControllerBlockEntity controller) {
                controller.removeBarrel(getBlockPos());
            }
            setControllerPos(null);
        }
    }

    private void invalidateCaches() {
        cachedUsedUpgradeSlots = cachedStorage = Utils.INVALID;
    }
    private int computeUsedUpgradeSlots() {
        int slots = 0;
        for (UpgradeStack entry : upgrades) {
            slots += entry.getUpgradeSlotsRequired();
        }
        if (isVoid()) slots++;
        return slots;
    }

    private int computeStorage() {
        int storage = Utils.BASE_STORAGE;
        for (UpgradeStack upgradeStack : upgrades) {
            storage += upgradeStack.getStorageStacks();
        }
        return storage;
    }

    public boolean isVoid() {
        return getBlockState().getValue(BetterBarrelBlock.VOID);
    }

    public List<UpgradeStack> getUpgrades() {
        return upgrades;
    }


    public ItemStack tryAddItem(ItemStack stack) {
        return barrelHandler.insertItem(0, stack, false);
    }
    public ItemStack tryRemoveItem() {
        return getBarrelHandler().extractItem(0,barrelHandler.getStack().getMaxStackSize(),false);
    }

    public boolean canAcceptUpgrade(UpgradeStack data) {

        int existing = countUpgrade(data.getData());
        int max = data.getData().getMaxStackSize();
        return existing + data.getCount() <= max && data.getUpgradeSlotsRequired() <= getFreeSlots();

    }

    public int countUpgrade(Upgrade data) {
        if (data == Upgrades.VOID) {
            return isVoid() ? 1 : 0;
        }
        for (UpgradeStack dataStack : getUpgrades()) {
            if (dataStack.getData() == data) return dataStack.getCount();
        }
        return 0;
    }

    public boolean hasUpgrade(Upgrade data) {
        return countUpgrade(data) > 0;
    }

    public void upgrade(UpgradeStack dataStack) {
        dataStack.getData().onUpgrade(this,dataStack);
        invalidateCaches();
        setChanged();
    }

    public int getTotalUpgradeSlots() {
        return ((BetterBarrelBlock)getBlockState().getBlock()).getBarrelTier().getUpgradeSlots();
    }


    public int getFreeSlots() {
        return getTotalUpgradeSlots() - getUsedSlots();
    }
    public boolean isDiscrete() {
        return getType() == ModBlockEntityTypes.DISCRETE_BETTER_BARREL;
    }

    public int getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }

    public boolean hasGhost() {
        return getBlockState().getValue(BetterBarrelBlock.LOCKED) && !ghost.isEmpty();
    }
    public ItemStack getGhost() {
        return ghost;
    }

    public void clearGhost() {
        ghost = ItemStack.EMPTY;
        setChanged();
    }

    public static void serverTick(Level pLevel1, BlockPos pPos, BlockState pState1, BetterBarrelBlockEntity pBlockEntity) {
        for (UpgradeStack upgradeData : pBlockEntity.getUpgrades()) {
            upgradeData.getData().tick(pBlockEntity,upgradeData);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(NBTKeys.Stack.name(), barrelHandler.getStack().save(new CompoundTag()));
        pTag.putInt(NBTKeys.RealCount.name(), barrelHandler.getStack().getCount());

        ListTag upgradesTag = new ListTag();

        for (UpgradeStack stack : upgrades) {
            CompoundTag tag = stack.save();
            upgradesTag.add(tag);
        }
        pTag.put(NBTKeys.Upgrades.name(), upgradesTag);
        pTag.putInt(NBTKeys.Color.name(), color);
        pTag.putDouble(NBTKeys.Size.name(), size);
        pTag.put(NBTKeys.Ghost.name(), ghost.save(new CompoundTag()));
        if (controllerPos != null) {
            pTag.putIntArray("Controller",new int[]{controllerPos.getX(),controllerPos.getY(),controllerPos.getZ()});
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ItemStack stack = ItemStack.of(pTag.getCompound(NBTKeys.Stack.name()));
        stack.setCount(pTag.getInt(NBTKeys.RealCount.name()));
        barrelHandler.setStack(stack);
        upgrades.clear();
        ListTag upgradesTag = pTag.getList(NBTKeys.Upgrades.name(), Tag.TAG_COMPOUND);
        for (Tag tag : upgradesTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            upgrades.add(UpgradeStack.of(compoundTag));
        }
        color = pTag.getInt(NBTKeys.Color.name());
        size = pTag.getDouble(NBTKeys.Size.name());
        ghost = ItemStack.of(pTag.getCompound(NBTKeys.Ghost.name()));

        if (pTag.contains("Controller")) {
            int[] contr = pTag.getIntArray("Controller");
            controllerPos = new BlockPos(contr[0],contr[1],contr[2]);
        }
        invalidateCaches();
    }

    public BarrelHandler getBarrelHandler() {
        return barrelHandler;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    public void setSize(double size) {
        this.size = size;
        setChanged();
    }

    public void searchForControllers() {
        List<BlockEntity> controllers = Utils.getNearbyControllers(level,getBlockPos());
        if (!controllers.isEmpty()) {
            BlockPos newController = null;
            if (controllers.size() == 1) {
                newController = controllers.get(0).getBlockPos();
            } else {
                int dist = Integer.MAX_VALUE;
                for (BlockEntity blockEntity : controllers) {
                    if (newController == null || blockEntity.getBlockPos().distManhattan(getBlockPos()) < dist) {
                        newController = blockEntity.getBlockPos();
                        dist = blockEntity.getBlockPos().distManhattan(getBlockPos());
                    }
                }
            }
            setControllerPos(newController);
        }
    }

    public static class BarrelHandler implements IItemHandler {
        private final BetterBarrelBlockEntity barrelBlockEntity;

        BarrelHandler(BetterBarrelBlockEntity barrelBlockEntity) {
            this.barrelBlockEntity = barrelBlockEntity;
        }

        private ItemStack stack = ItemStack.EMPTY;

        @Override
        public int getSlots() {
            return 1;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return stack;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || !isItemValid(slot, stack)) return stack;

            //reverse the "trick" we did earlier

            int limit = getSlotLimit(slot) - (barrelBlockEntity.isVoid() ? 1 : 0);
            int count = stack.getCount();
            int existing = this.stack.isEmpty() ? 0 : this.stack.getCount();
            if (count + existing > limit) {
                if (!simulate) {
                    this.stack = ItemHandlerHelper.copyStackWithSize(stack, limit);
                    markDirty();
                }
                return barrelBlockEntity.isVoid() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, count + existing - limit);
            } else {
                if (!simulate) {
                    this.stack = ItemHandlerHelper.copyStackWithSize(stack, existing + count);
                    markDirty();
                }
                return ItemStack.EMPTY;
            }
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0 || stack.isEmpty()) return ItemStack.EMPTY;

            //handling infinite vending is easy
            if (barrelBlockEntity.hasUpgrade(Upgrades.INFINITE_VENDING)) {
                return ItemHandlerHelper.copyStackWithSize(this.stack,amount);
            }

            int existing = stack.getCount();
            ItemStack newStack;
            if (amount > existing) {
                newStack = ItemHandlerHelper.copyStackWithSize(stack, existing);
                if (!simulate) {
                    barrelBlockEntity.ghost = ItemHandlerHelper.copyStackWithSize(stack,1);

                    setStack(ItemStack.EMPTY);
                }
            } else {
                newStack = ItemHandlerHelper.copyStackWithSize(stack, amount);
                if (!simulate) {
                    if (amount == existing) {
                        barrelBlockEntity.ghost = ItemHandlerHelper.copyStackWithSize(stack,1);
                    }
                    stack.shrink(amount);
                }
            }
            if (!simulate) {
                markDirty();
            }
            return newStack;
        }
        @Override
        public int getSlotLimit(int slot) {//have to trick the vanilla hopper into inserting so voiding work
            return barrelBlockEntity.getStorage() * 64 + (barrelBlockEntity.isVoid() ? 1 : 0);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack incoming) {
            return (!barrelBlockEntity.hasGhost() || ItemStack.isSameItemSameTags(incoming, barrelBlockEntity.getGhost()))
                    && (this.stack.isEmpty() || ItemStack.isSameItemSameTags(this.stack, incoming));
        }

        public void markDirty() {
            barrelBlockEntity.setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        //let the client know the block changed
        level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),3);
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

    private LazyOptional<IItemHandler> optional = LazyOptional.of(this::getBarrelHandler);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? optional.cast() : super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optional.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        optional = LazyOptional.of(this::getBarrelHandler);
    }
}
