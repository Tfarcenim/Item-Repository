package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.util.FabricUtils;

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

    public ControllerBlockEntity.ControllerHandler getHandler() {
        ControllerBlockEntity controllerBlockEntity = getController();
        if (controllerBlockEntity == null) {
            return null;
        }
        return controllerBlockEntity.getHandler();
    }
}
