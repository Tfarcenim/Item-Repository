package tfar.nabba.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.api.*;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.blockentity.*;
import tfar.nabba.shim.IFluidHandlerShim;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class FabricUtils {


    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> add_to_internal_upgrades = (betterBarrelBlockEntity, upgradeData) -> {

        int existing = betterBarrelBlockEntity.countUpgrade(upgradeData.getData());
        if (existing == 0) {
            betterBarrelBlockEntity.getUpgrades().add(upgradeData.copy());//do not use the original
        } else {
            for (UpgradeStack upgradeStack : betterBarrelBlockEntity.getUpgrades()) {
                if (upgradeStack.getData() == upgradeData.getData()) {
                    upgradeStack.grow(upgradeData.getCount());
                    break;
                }
            }
        }
    };

    public static BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> createConsumer(BooleanProperty property) {
        return (betterBarrelBlockEntity, upgradeData) -> {
            BlockState state = betterBarrelBlockEntity.getBlockState();
            betterBarrelBlockEntity.getLevel().setBlock(betterBarrelBlockEntity.getBlockPos(), state.setValue(property, true), 3);
        };
    }

    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> apply_void = createConsumer(AbstractBarrelBlock.VOID);
    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> apply_infinite_vending = createConsumer(SingleSlotBarrelBlock.INFINITE_VENDING);
    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> apply_storage_downgrade = createConsumer(SingleSlotBarrelBlock.STORAGE_DOWNGRADE);
    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> apply_redstone = createConsumer(AbstractBarrelBlock.REDSTONE);

    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> PICKUP_TICK =
            (betterBarrelBlockEntity, upgradeDataStack) -> pickupInABox(betterBarrelBlockEntity,
                    2 * upgradeDataStack.getCount() - 1, 3, 2 * upgradeDataStack.getCount() - 1);

    public static void pickupInABox(AbstractBarrelBlockEntity betterBarrelBlockEntity, int x, int y, int z) {
        Level level = betterBarrelBlockEntity.getLevel();

        if (betterBarrelBlockEntity instanceof HasHandler hasHandler && !hasHandler.isFull()) {

            if (betterBarrelBlockEntity instanceof HasItemHandler) {

                //note, AABBs start at 0,0,0 on the blockEntity, so to get a 3x3x3 cube we need to go from -1,-1,-1 to +2,+2,+2 relative
                List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class,
                        getBoxCenteredOn(betterBarrelBlockEntity.getBlockPos(), x, y, z)
                );
                for (ItemEntity itemEntity : itemEntities) {
                    addItem((HasItemHandler) betterBarrelBlockEntity, itemEntity);
                }
            } else if (betterBarrelBlockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {

                BlockPos.betweenClosedStream(
                        betterBarrelBlockEntity.getBlockPos().offset(-(x - 1) / 2, -(y - 1) / 2, -(z - 1) / 2),
                        betterBarrelBlockEntity.getBlockPos().offset((x - 1) / 2, (y - 1) / 2, (z - 1) / 2)
                ).forEachOrdered(pos -> {
                    FluidState fluidState = level.getFluidState(pos);

                    FabricFluidStack fabricFluidStack = new FabricFluidStack(FluidVariant.of(fluidState.getType()), FluidConstants.BUCKET);
                    if (fluidBarrelBlockEntity.isValid(fabricFluidStack)) {
                        Fluid fluid = fluidState.getType();

                        if (fluid instanceof FlowingFluid flowingFluid && flowingFluid.canConvertToSource(level)) {
                            FluidMovingUtil.interactWithFluidStorageFill(fluidBarrelBlockEntity.getFluidStorage(),fabricFluidStack);
                        } else {
                            BlockState state = level.getBlockState(pos);
                            if (state.getBlock() instanceof BucketPickup bucketPickup) {
                                ItemStack filled = bucketPickup.pickupBlock(level, pos, state);
                                //todo
                               // FluidUtil.tryEmptyContainer(filled, fluidBarrelBlockEntity.getFluidHandler(), Integer.MAX_VALUE, null, true);
                            }
                        }
                    }
                });
            }
        }
    }

    public static AABB getBoxCenteredOn(BlockPos pos, int side) {
        return getBoxCenteredOn(pos, side, side, side);
    }

    public static AABB getBoxCenteredOn(BlockPos pos, int x, int y, int z) {
        return new AABB(pos).inflate((x - 1) / 2d, (y - 1) / 2d, (z - 1) / 2d);
    }


    public static boolean addItem(HasItemHandler betterBarrelBlockEntity, ItemEntity pItem) {
        boolean flag = false;
        ItemStack itemstack = pItem.getItem().copy();
        ItemStack itemstack1 = betterBarrelBlockEntity.tryAddItem(itemstack);
        if (itemstack1.isEmpty()) {
            flag = true;
            pItem.discard();
        } else {
            pItem.setItem(itemstack1);
        }
        return flag;
    }

    public static List<BlockEntity> getNearbyFreeBarrels(Level level, BlockPos thisPos) {
        return CommonUtils.getNearbyBlockEntities(level, isFreeAndControllableBarrel, thisPos);
    }

    public static List<BlockEntity> getNearbyControllers(Level level, BlockPos thisPos) {
        return CommonUtils.getNearbyBlockEntities(level, isController, thisPos);
    }

    public static List<BlockEntity> getNearbyProxies(Level level, BlockPos thisPos) {
        return CommonUtils.getNearbyBlockEntities(level, isControllerProxy, thisPos);
    }

    public static final Predicate<BlockEntity> isController = ControllerBlockEntity.class::isInstance;

    public static final Predicate<BlockEntity> isControllerProxy = ControllerProxyBlockEntity.class::isInstance;

    public static final Predicate<BlockEntity> isFreeAndControllableBarrel = blockEntity -> blockEntity instanceof SingleSlotBarrelBlockEntity<?> singleSlotBarrelBlockEntity && singleSlotBarrelBlockEntity.canConnect();


    public static boolean isFluidValid(FabricFluidStack existing, @NotNull FabricFluidStack incoming, FabricFluidStack ghost) {
        return (ghost.isEmpty() || incoming.equals(ghost))
                && (existing.isEmpty() || existing.equals(incoming));
    }


    public static int getRedstoneSignalFromContainer(ItemHandler pContainer) {
        ItemStack itemstack = pContainer.getStackInSlot(0);
        if (!itemstack.isEmpty()) {
            float f = (float) itemstack.getCount() / (float) pContainer.getActualLimit();
            return Mth.floor(f * 14.0F) + 1;
        } else {
            return 0;
        }
    }

    public static int getRedstoneSignalFromContainer(IFluidHandlerShim pContainer) {
        FabricFluidStack fluid = pContainer.getFluidInTank(0);
        if (!fluid.isEmpty()) {
            float f = (float) fluid.getAmount() / (float) pContainer.getTankCapacity(0);
            return Mth.floor(f * 14.0F) + 1;
        } else {
            return 0;
        }

    }

    public static int getRedstoneSignalFromAntibarrel(AntiBarrelBlockEntity.AntiBarrelInventory pContainer) {
        int stored = pContainer.getStoredCount();
        if (stored == 0) return 0;
        int max = pContainer.getActualLimit();
        float f = (float) stored / max;
        return Mth.floor(f * 14) + 1;
    }
}
