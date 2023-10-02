package tfar.nabba.blockentity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.NABBAFabric;
import tfar.nabba.api.HasFluidHandler;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.FabricFluidUtils;
import tfar.nabba.util.FabricUtils;
import tfar.nabba.util.NBTKeys;

public class FluidBarrelBlockEntity extends SingleSlotBarrelBlockEntity<FabricFluidStack> implements HasFluidHandler {

    protected FluidBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new FluidBarrelHandler(this);
        ghost = FabricFluidStack.empty();
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
        ghost = FabricFluidStack.empty();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(NBTKeys.Stack.name(), barrelHandler.getFluid().toTag());
        pTag.put(NBTKeys.Ghost.name(), ghost.toTag());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        FabricFluidStack stack = FabricFluidStack.of(pTag.getCompound(NBTKeys.Stack.name()));
        barrelHandler.setFluid(stack);
        ghost = FabricFluidStack.of(pTag.getCompound(NBTKeys.Ghost.name()));
    }

    public FluidBarrelHandler getFluidHandler() {
        return barrelHandler;
    }

    public static class FluidBarrelHandler implements IFluidHandlerShim {
        private final FluidBarrelBlockEntity barrelBlockEntity;

        FluidBarrelHandler(FluidBarrelBlockEntity barrelBlockEntity) {
            this.barrelBlockEntity = barrelBlockEntity;
        }

        private FabricFluidStack stack = FabricFluidStack.empty();
        long amount = 0;

        @Override
        public int getTanks() {
            return 1;
        }

        public FabricFluidStack getFluid() {
            return stack;
        }

        public void setFluid(FabricFluidStack stack) {
            this.stack = stack;
        }

        @Override
        public @NotNull FabricFluidStack getFluidInTank(int slot) {
            return stack;
        }

        @Override
        public long fill(@NotNull FabricFluidStack incoming, FluidAction action) {
            if (incoming.isEmpty() || !isFluidValid(incoming)) return 0;

            long limit = getActualCapacity(0);
            long existing = amount;
            long count = incoming.getAmount();
            boolean isVoid = barrelBlockEntity.isVoid();
            if (existing >= limit) {
                return isVoid ? count : 0;
            }

            else if (count + existing > limit) {
                if (action.execute()) {
                    this.stack = FabricFluidUtils.copyFluidStackWithSize(incoming, limit);
                    markDirty();
                }
                return isVoid ? count : limit - existing;
            } else {
                if (action.execute()) {
                    this.stack = FabricFluidUtils.copyFluidStackWithSize(incoming, existing + count);
                    markDirty();
                }
                return count;
            }
        }

        @Override
        public @NotNull FabricFluidStack drain(FabricFluidStack resource, FluidAction action) {
            if (isFluidValid(resource)) {
                return drain(resource.getAmount(),action);
            }
            return FabricFluidStack.empty();
        }

        @Override
        public @NotNull FabricFluidStack drain(long amount,FluidAction action) {
            if (amount == 0 || stack.isEmpty()) return FabricFluidStack.empty();

            //handling infinite vending is easy
            if (barrelBlockEntity.infiniteVending()) {
                return FabricFluidUtils.copyFluidStackWithSize(this.stack,amount);
            }

            long existing = stack.getAmount();
            FabricFluidStack newStack;
            if (amount > existing) {
                newStack = FabricFluidUtils.copyFluidStackWithSize(stack, existing);
                if (action.execute()) {
                    barrelBlockEntity.ghost = FabricFluidUtils.copyFluidStackWithSize(stack,1);
                    setFluid(FabricFluidStack.empty());
                }
            } else {
                newStack = FabricFluidUtils.copyFluidStackWithSize(stack, amount);
                if (action.execute()) {
                    if (amount == existing) {
                        barrelBlockEntity.ghost = FabricFluidUtils.copyFluidStackWithSize(stack,1);
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
        public ItemStack getContainer() {
            return null;
        }

        @Override
        public long getTankCapacity(int slot) {
            return getActualCapacity(slot) + (barrelBlockEntity.isVoid() ? 1 : 0);
        }

        public long getActualCapacity(int tank) {
            return barrelBlockEntity.getStorageMultiplier() * FluidConstants.BUCKET *
                    (barrelBlockEntity.hasDowngrade() ? 1 : NABBAFabric.ServerCfg.fluid_barrel_base_storage);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FabricFluidStack stack) {
            return isFluidValid(stack);
        }

        public boolean isFluidValid(@NotNull FabricFluidStack incoming) {
            return (!barrelBlockEntity.hasGhost() || incoming.equals(barrelBlockEntity.getGhost()))
                    && (this.stack.isEmpty() || this.stack.equals(incoming));
        }

        public void markDirty() {
            barrelBlockEntity.setChanged();
        }
    }

    public Storage<FluidVariant> getFluidStorage() {
        return new SingleBarrelFluidStorage(this);
    }

    @Override
    public int getRedstoneOutput() {
        return FabricUtils.getRedstoneSignalFromContainer(getFluidHandler());
    }
}
