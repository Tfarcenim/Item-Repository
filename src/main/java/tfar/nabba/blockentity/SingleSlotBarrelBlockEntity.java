package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.util.Utils;

import java.util.List;

public abstract class SingleSlotBarrelBlockEntity<T> extends AbstractBarrelBlockEntity {

    protected T ghost;
    protected BlockPos controllerPos;

    public SingleSlotBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public abstract boolean hasGhost();

    public T getGhost() {
        return ghost;
    }

    public boolean isLocked() {
        return getBlockState().getValue(SingleSlotBarrelBlock.LOCKED);
    }

    public abstract void clearGhost();

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void removeController() {
        if (controllerPos != null) {
            BlockEntity blockEntity = level.getBlockEntity(getControllerPos());
            if (blockEntity instanceof ControllerBlockEntity controller) {
                controller.removeBarrel(getBlockPos(), getBarrelType());
                controller.synchronize();
            }
            setControllerPos(null);
        }
    }

    public boolean canConnect() {
        return getBlockState().getValue(SingleSlotBarrelBlock.CONNECTED);
    }

    public void searchForControllers() {
        List<BlockEntity> controllers = Utils.getNearbyControllers(level, getBlockPos());
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
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (getControllerPos() != null) {
            pTag.putIntArray("Controller", new int[]{getControllerPos().getX(), controllerPos.getY(), controllerPos.getZ()});
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("Controller")) {
            int[] contr = pTag.getIntArray("Controller");
            controllerPos = new BlockPos(contr[0], contr[1], contr[2]);
        }
    }
}
