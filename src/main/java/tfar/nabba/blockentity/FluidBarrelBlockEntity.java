package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasFluidHandler;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.inventory.ImmutableFluidStack;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Upgrades;
import tfar.nabba.util.Utils;

public class FluidBarrelBlockEntity extends AbstractBarrelBlockEntity implements HasFluidHandler {
    private FluidStack ghost = FluidStack.EMPTY;


    protected FluidBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new FluidBarrelHandler(this);
    }

    private final FluidBarrelHandler barrelHandler;

    public static FluidBarrelBlockEntity create(BlockPos pos, BlockState state) {
        return new FluidBarrelBlockEntity(ModBlockEntityTypes.FLUID_BARREL, pos, state);
    }

    public static FluidBarrelBlockEntity createDiscrete(BlockPos pos, BlockState state) {
        return new FluidBarrelBlockEntity(ModBlockEntityTypes.DISCRETE_FLUID_BARREL, pos, state);
    }

    public int tryAddFluid(FluidStack stack) {
        return barrelHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }
    public FluidStack tryRemoveFluid() {
        return barrelHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
    }

    public boolean hasGhost() {
        return getBlockState().getValue(BetterBarrelBlock.LOCKED) && !ghost.isEmpty();
    }
    public FluidStack getGhost() {
        return ghost;
    }

    public void clearGhost() {
        ghost = FluidStack.EMPTY;
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
        pTag.put(NBTKeys.Stack.name(), barrelHandler.getFluid().writeToNBT(new CompoundTag()));
        pTag.put(NBTKeys.Ghost.name(), ghost.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        FluidStack stack = FluidStack.loadFluidStackFromNBT(pTag.getCompound(NBTKeys.Stack.name()));
        barrelHandler.setFluid(stack);
        ghost = FluidStack.loadFluidStackFromNBT(pTag.getCompound(NBTKeys.Ghost.name()));
    }

    public FluidBarrelHandler getFluidHandler() {
        return barrelHandler;
    }

    public static class FluidBarrelHandler implements IFluidHandler {
        private final FluidBarrelBlockEntity barrelBlockEntity;

        FluidBarrelHandler(FluidBarrelBlockEntity barrelBlockEntity) {
            this.barrelBlockEntity = barrelBlockEntity;
        }

        private FluidStack stack = FluidStack.EMPTY;

        @Override
        public int getTanks() {
            return 1;
        }

        public FluidStack getFluid() {
            return stack;
        }

        public void setFluid(FluidStack stack) {
            this.stack = stack;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int slot) {
            return ImmutableFluidStack.of(stack);//do not allow modification
        }

        @Override
        public int fill(@NotNull FluidStack stack,FluidAction action) {
            if (stack.isEmpty() || !isFluidValid(stack)) return 0;

            int limit = getTankCapacity(0);
            int existing = this.stack.isEmpty() ? 0 : this.stack.getAmount();
            int count = stack.getAmount();
            if (existing >= limit) return 0;

            else if (count + existing > limit) {
                if (action.execute()) {
                    this.stack = Utils.copyFluidWithSize(stack, limit);
                    markDirty();
                }
                return barrelBlockEntity.isVoid() ? count : limit - existing;
            } else {
                if (action.execute()) {
                    this.stack = Utils.copyFluidWithSize(stack, existing + count);
                    markDirty();
                }
                return count;
            }
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (isFluidValid(resource)) {
                return drain(resource.getAmount(),action);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int amount,FluidAction action) {
            if (amount == 0 || stack.isEmpty()) return FluidStack.EMPTY;

            //handling infinite vending is easy
            if (barrelBlockEntity.hasUpgrade(Upgrades.INFINITE_VENDING)) {
                return Utils.copyFluidWithSize(this.stack,amount);
            }

            int existing = stack.getAmount();
            FluidStack newStack;
            if (amount > existing) {
                newStack = Utils.copyFluidWithSize(stack, existing);
                if (action.execute()) {
                    barrelBlockEntity.ghost = Utils.copyFluidWithSize(stack,1);

                    setFluid(FluidStack.EMPTY);
                }
            } else {
                newStack = Utils.copyFluidWithSize(stack, amount);
                if (action.execute()) {
                    if (amount == existing) {
                        barrelBlockEntity.ghost = Utils.copyFluidWithSize(stack,1);
                    }
                    stack.shrink(amount);
                }
            }
            if (action.execute()) {
                markDirty();
            }
            return newStack;
        }

        @Override
        public int getTankCapacity(int slot) {//have to trick the vanilla hopper into inserting so voiding work
            return barrelBlockEntity.getStorage() * 1000 + (barrelBlockEntity.isVoid() ? 1 : 0);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return isFluidValid(stack);
        }

        public boolean isFluidValid(@NotNull FluidStack incoming) {
            return (!barrelBlockEntity.hasGhost() || incoming.isFluidEqual(barrelBlockEntity.getGhost()))
                    && (this.stack.isEmpty() || this.stack.isFluidEqual(incoming));
        }

        public void markDirty() {
            barrelBlockEntity.setChanged();
        }
    }
    private LazyOptional<IFluidHandler> optional = LazyOptional.of(this::getFluidHandler);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.FLUID_HANDLER ? optional.cast() : super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optional.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        optional = LazyOptional.of(this::getFluidHandler);
    }
}
