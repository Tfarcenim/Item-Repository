package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.Upgrade;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.item.StorageUpgradeItem;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Upgrades;
import tfar.nabba.util.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBarrelBlockEntity extends BlockEntity {

    private transient int cachedStorage = Utils.INVALID;
    private transient int cachedUsedUpgradeSlots = Utils.INVALID;
    protected BlockPos controllerPos;
    protected int color = 0xff99ff;
    protected double size = .5;
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

    public boolean isValid(UpgradeItem item) {
        if (item instanceof StorageUpgradeItem storageUpgradeItem) {
            return storageUpgradeItem.getType() == this.getBarrelType();
        }
        return true;
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

    public int getColor() {
        return color;
    }

    public double getSize() {
        return size;
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


    private int computeStorageUnits() {
        int storage = Utils.BASE_STORAGE.get(getBarrelType());
        for (UpgradeStack upgradeStack : upgrades) {
            storage += upgradeStack.getStorageUnits(getBarrelType());
        }
        return storage;
    }

    public IItemHandler getItemHandler() {
        return EmptyHandler.INSTANCE;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void setChanged() {
        super.setChanged();
        //let the client know the block changed
        level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),3);
    }
    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    BarrelType getBarrelType() {
        return ((AbstractBarrelBlock)getBlockState().getBlock()).getType();
    }

    public int getStorage() {
        if (cachedStorage == -1) {//save CPU cycles by not iterating the upgrade map
            cachedStorage = computeStorageUnits();
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

    public void searchForControllers() {
        List<BlockEntity> controllers = Utils.getNearbyControllers(level,getBlockPos());
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

    protected void invalidateCaches() {
        cachedUsedUpgradeSlots = cachedStorage = Utils.INVALID;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ListTag upgradesTag = new ListTag();

        for (UpgradeStack stack : getUpgrades()) {
            CompoundTag tag = stack.save();
            upgradesTag.add(tag);
        }
        pTag.put(NBTKeys.Upgrades.name(), upgradesTag);
        if (getControllerPos() != null) {
            pTag.putIntArray("Controller",new int[]{getControllerPos().getX(),controllerPos.getY(),controllerPos.getZ()});
        }
        pTag.putInt(NBTKeys.Color.name(), getColor());
        pTag.putDouble(NBTKeys.Size.name(), getSize());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        getUpgrades().clear();
        ListTag upgradesTag = pTag.getList(NBTKeys.Upgrades.name(), Tag.TAG_COMPOUND);
        for (Tag tag : upgradesTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            getUpgrades().add(UpgradeStack.of(compoundTag));
        }

        if (pTag.contains("Controller")) {
            int[] contr = pTag.getIntArray("Controller");
            controllerPos = new BlockPos(contr[0],contr[1],contr[2]);
        }
        color = pTag.getInt(NBTKeys.Color.name());
        size = pTag.getDouble(NBTKeys.Size.name());
    }
}
