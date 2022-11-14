package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.Upgrade;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.util.Upgrades;
import tfar.nabba.util.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBarrelBlockEntity extends BlockEntity {

    private transient int cachedStorage = Utils.INVALID;
    private transient int cachedUsedUpgradeSlots = Utils.INVALID;
    protected BlockPos controllerPos;
    public AbstractBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    private List<UpgradeStack> upgrades = new ArrayList<>();

    private int computeUsedUpgradeSlots() {
        int slots = 0;
        for (UpgradeStack entry : upgrades) {
            slots += entry.getUpgradeSlotsRequired();
        }
        if (isVoid()) slots++;
        return slots;
    }

    public final boolean isVoid() {
        return getBlockState().getValue(AbstractBarrelBlock.VOID);
    }

    public boolean canAcceptUpgrade(UpgradeStack data) {

        int existing = countUpgrade(data.getData());
        int max = data.getData().getMaxStackSize();
        return existing + data.getCount() <= max && data.getUpgradeSlotsRequired() <= getFreeSlots();

    }

    public ItemStack tryAddItem(ItemStack stack) {
        return stack;//barrelHandler.insertItem(0, stack, false);
    }
    public ItemStack tryRemoveItem() {
        return ItemStack.EMPTY;//getBarrelHandler().extractItem(0,barrelHandler.getStack().getMaxStackSize(),false);
    }

    public final List<UpgradeStack> getUpgrades() {
        return upgrades;
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

    public final int getTotalUpgradeSlots() {
        return ((AbstractBarrelBlock)getBlockState().getBlock()).getBarrelTier().getUpgradeSlots();
    }


    public int getFreeSlots() {
        return getTotalUpgradeSlots() - getUsedSlots();
    }


    private int computeStorage() {
        int storage = Utils.BASE_STORAGE;
        for (UpgradeStack upgradeStack : upgrades) {
            storage += upgradeStack.getStorageStacks();
        }
        return storage;
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

    protected void invalidateCaches() {
        cachedUsedUpgradeSlots = cachedStorage = Utils.INVALID;
    }
}
