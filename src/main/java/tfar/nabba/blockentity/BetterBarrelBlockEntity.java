package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Upgrades;

public class BetterBarrelBlockEntity extends AbstractBarrelBlockEntity {
    private ItemStack ghost = ItemStack.EMPTY;


    protected BetterBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new BarrelHandler(this);
    }

    private final BarrelHandler barrelHandler;

    public static BetterBarrelBlockEntity create(BlockPos pos, BlockState state) {
        return new BetterBarrelBlockEntity(ModBlockEntityTypes.BETTER_BARREL, pos, state);
    }

    public static BetterBarrelBlockEntity createDiscrete(BlockPos pos, BlockState state) {
        return new BetterBarrelBlockEntity(ModBlockEntityTypes.DISCRETE_BETTER_BARREL, pos, state);
    }

    public ItemStack tryAddItem(ItemStack stack) {
        return barrelHandler.insertItem(0, stack, false);
    }
    public ItemStack tryRemoveItem() {
        return getItemHandler().extractItem(0,barrelHandler.getStack().getMaxStackSize(),false);
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

    public static void serverTick(Level pLevel1, BlockPos pPos, BlockState pState1, AbstractBarrelBlockEntity pBlockEntity) {
        for (UpgradeStack upgradeData : pBlockEntity.getUpgrades()) {
            upgradeData.getData().tick(pBlockEntity,upgradeData);
        }
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
        invalidateCaches();
    }

    public BarrelHandler getItemHandler() {
        return barrelHandler;
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
}
