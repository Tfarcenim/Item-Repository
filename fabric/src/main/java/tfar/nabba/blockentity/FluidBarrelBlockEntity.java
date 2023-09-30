package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.HasFluidHandler;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.inventory.ImmutableFluidStack;
import tfar.nabba.util.FabricUtils;

public class FluidBarrelBlockEntity extends SingleSlotBarrelBlockEntity<FluidStack> implements HasFluidHandler {

    protected FluidBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new FluidBarrelHandler(this);
        ghost = FluidStack.EMPTY;
    }

    private final FluidBarrelHandler barrelHandler;

    public static FluidBarrelBlockEntity create(BlockPos pos, BlockState state) {
        return new FluidBarrelBlockEntity(ModBlockEntityTypes.FLUID_BARREL, pos, state);
    }

    public static FluidBarrelBlockEntity createDiscrete(BlockPos pos, BlockState state) {
        return new FluidBarrelBlockEntity(ModBlockEntityTypes.DISCRETE_FLUID_BARREL, pos, state);
    }

    public boolean hasGhost() {
        return getBlockState().getValue(BetterBarrelBlock.LOCKED) && !ghost.isEmpty();
    }

    public void clearGhost() {
        ghost = FluidStack.EMPTY;
        setChanged();
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
        public int fill(@NotNull FluidStack incoming,FluidAction action) {
            if (incoming.isEmpty() || !isFluidValid(incoming)) return 0;

            int limit = getActualCapacity(0);
            int existing = this.stack.getAmount();
            int count = incoming.getAmount();
            boolean isVoid = barrelBlockEntity.isVoid();
            if (existing >= limit) {
                return isVoid ? count : 0;
            }

            else if (count + existing > limit) {
                if (action.execute()) {
                    this.stack = FabricUtils.copyFluidWithSize(incoming, limit);
                    markDirty();
                }
                return isVoid ? count : limit - existing;
            } else {
                if (action.execute()) {
                    this.stack = FabricUtils.copyFluidWithSize(incoming, existing + count);
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
            if (barrelBlockEntity.infiniteVending()) {
                return FabricUtils.copyFluidWithSize(this.stack,amount);
            }

            int existing = stack.getAmount();
            FluidStack newStack;
            if (amount > existing) {
                newStack = FabricUtils.copyFluidWithSize(stack, existing);
                if (action.execute()) {
                    barrelBlockEntity.ghost = FabricUtils.copyFluidWithSize(stack,1);
                    setFluid(FluidStack.EMPTY);
                }
            } else {
                newStack = FabricUtils.copyFluidWithSize(stack, amount);
                if (action.execute()) {
                    if (amount == existing) {
                        barrelBlockEntity.ghost = FabricUtils.copyFluidWithSize(stack,1);
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
        public int getTankCapacity(int slot) {
            return getActualCapacity(slot) + (barrelBlockEntity.isVoid() ? 1 : 0);
        }

        public int getActualCapacity(int tank) {
            return barrelBlockEntity.getStorageMultiplier() * 1000 *
                    (barrelBlockEntity.hasDowngrade() ? 1 : NABBA.ServerCfg.fluid_barrel_base_storage.get());
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

    @Override
    public int getRedstoneOutput() {
        return FabricUtils.getRedstoneSignalFromContainer(getFluidHandler());
    }
}
