package tfar.nabba.blockentity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.inventory.BetterBarrelSlotWrapper;
import tfar.nabba.inventory.FluidBarrelSlotWrapper;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.FabricUtils;

import java.util.ArrayList;
import java.util.List;

public class ControllerProxyBlockEntity extends BlockEntity {
    protected BlockPos controllerPos;

    protected ControllerProxyBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);

    }

    public static ControllerProxyBlockEntity create(BlockPos pos, BlockState state) {
        return new ControllerProxyBlockEntity(ModBlockEntityTypes.CONTROLLER_PROXY, pos, state);
    }

    public void searchForControllers() {
        List<BlockEntity> controllers = FabricUtils.getNearbyControllers(level, getBlockPos());
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



    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (getControllerPos() != null) {
            pTag.putIntArray("Controller", new int[]{getControllerPos().getX(), controllerPos.getY(), controllerPos.getZ()});
        }
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public ControllerBlockEntity getController() {
        if (getControllerPos() == null) {
            return null;
        }
        BlockEntity be = level.getBlockEntity(getControllerPos());
        if (be instanceof ControllerBlockEntity controller) {
            return controller;
        } else {
            removeController();
            return null;
        }
    }

    public void removeController() {
        setControllerPos(null);
        setChanged();
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;

        if (controllerPos != null) {
            BlockEntity blockEntity = level.getBlockEntity(getControllerPos());
            if (blockEntity instanceof ControllerBlockEntity controller) {
                controller.addBarrel(this);
                controller.synchronize();
            }
        }
        setChanged();
    }


    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("Controller")) {
            int[] contr = pTag.getIntArray("Controller");
            controllerPos = new BlockPos(contr[0], contr[1], contr[2]);
        }
    }


    //fluid api

    private CombinedStorage<FluidVariant, FluidBarrelSlotWrapper> fluidStorage;

    public CombinedStorage<FluidVariant, FluidBarrelSlotWrapper> getFluidStorage(Direction direction) {
        ControllerBlockEntity controllerBlockEntity = getController();
        if (controllerBlockEntity == null) return null;

        if (fluidStorage == null) {
            fluidStorage = controllerBlockEntity.getFluidStorage(direction);
        }
        return fluidStorage;
    }

    //item api

    private CombinedStorage<ItemVariant, BetterBarrelSlotWrapper> itemStorage;

    public CombinedStorage<ItemVariant,BetterBarrelSlotWrapper> getItemStorage(Direction direction) {

        ControllerBlockEntity controllerBlockEntity = getController();

        if (controllerBlockEntity == null) {
            return null;
        }
        if (itemStorage == null) {
            itemStorage = controllerBlockEntity.getItemStorage(direction);//delegate to connected controller
        }
        return itemStorage;
    }
}
