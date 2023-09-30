package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.HasItemHandler;
import tfar.nabba.api.ItemHandler;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.util.CommonUtils;
import tfar.nabba.util.FabricUtils;

public class BetterBarrelBlockEntity extends SingleSlotBarrelBlockEntity<ItemStack> implements HasItemHandler {


    protected BetterBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new BarrelHandler(this);
        ghost = ItemStack.EMPTY;
    }

    private final BarrelHandler barrelHandler;

    public static BetterBarrelBlockEntity create(BlockPos pos, BlockState state) {
        return new BetterBarrelBlockEntity(ModBlockEntityTypes.BETTER_BARREL, pos, state);
    }

    public static BetterBarrelBlockEntity createDiscrete(BlockPos pos, BlockState state) {
        return new BetterBarrelBlockEntity(ModBlockEntityTypes.DISCRETE_BETTER_BARREL, pos, state);
    }

    public ItemStack tryRemoveItem() {
        return getItemHandler().extractItem(0,barrelHandler.getStack().getMaxStackSize(),false);
    }

    public boolean hasGhost() {
        return getBlockState().getValue(BetterBarrelBlock.LOCKED) && !ghost.isEmpty();
    }

    public void clearGhost() {
        ghost = ItemStack.EMPTY;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(NBTKeys.Stack.name(), barrelHandler.getStack().save(new CompoundTag()));
        pTag.putInt(NBTKeys.RealCount.name(), barrelHandler.getStack().getCount());
        pTag.put(NBTKeys.Ghost.name(), ghost.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ItemStack stack = ItemStack.of(pTag.getCompound(NBTKeys.Stack.name()));
        stack.setCount(pTag.getInt(NBTKeys.RealCount.name()));
        barrelHandler.setStack(stack);
        ghost = ItemStack.of(pTag.getCompound(NBTKeys.Ghost.name()));
    }

    public BarrelHandler getItemHandler() {
        return barrelHandler;
    }

    public static class BarrelHandler implements ItemHandler {
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
        public boolean isFull() {
            return getSlotLimit(0) <= getStackInSlot(0).getCount();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || !isItemValid(slot, stack)) return stack;

            int limit = getActualLimit();
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
            if (barrelBlockEntity.infiniteVending()) {
                return ItemHandlerHelper.copyStackWithSize(this.stack,amount);
            }

            int existing = stack.getCount();
            ItemStack newStack;
            if (amount >= existing) {
                newStack = ItemHandlerHelper.copyStackWithSize(stack, existing);
                if (!simulate) {
                    barrelBlockEntity.ghost = barrelBlockEntity.isLocked() ? ItemHandlerHelper.copyStackWithSize(stack,1): ItemStack.EMPTY;
                    setStack(ItemStack.EMPTY);
                }
            } else {
                newStack = ItemHandlerHelper.copyStackWithSize(stack, amount);
                if (!simulate) {
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
            return getActualLimit() + (barrelBlockEntity.isVoid() ? 1 : 0);
        }

        public int getActualLimit() {
            return barrelBlockEntity.getStorageMultiplier() * stack.getMaxStackSize() *
                    (barrelBlockEntity.hasDowngrade() ? 1 : NABBA.ServerCfg.better_barrel_base_storage.get());
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack incoming) {
            return CommonUtils.isItemValid(this.stack,incoming,barrelBlockEntity.ghost);
        }

        public void markDirty() {
            barrelBlockEntity.setChanged();
        }
    }
    private LazyOptional<IItemHandler> optional = LazyOptional.of(this::getItemHandler);
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
        optional = LazyOptional.of(this::getItemHandler);
    }

    @Override
    public int getRedstoneOutput() {
        return FabricUtils.getRedstoneSignalFromContainer(getItemHandler());
    }
}
