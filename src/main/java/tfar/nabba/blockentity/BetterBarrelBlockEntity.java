package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.UpgradeData;
import tfar.nabba.util.Utils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BetterBarrelBlockEntity extends BlockEntity {

    private Map<UpgradeItem, Integer> upgrades = new HashMap<>();
    private transient int cachedStorage = Utils.INVALID;
    private transient int cachedUsedUpgradeSlots = Utils.INVALID;

    public BetterBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new BarrelHandler(this);
    }

    private final BarrelHandler barrelHandler;

    public BetterBarrelBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.BETTER_BARREL, pos, state);
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

    private void invalidateCaches() {
        cachedUsedUpgradeSlots = cachedStorage = Utils.INVALID;
    }
    private int computeUsedUpgradeSlots() {
        int slots = 0;
        for (Map.Entry<UpgradeItem, Integer> entry : upgrades.entrySet()) {
            slots += entry.getKey().getData().getSlotRequirement() * entry.getValue();
        }
        return slots;
    }

    private int computeStorage() {
        int storage = Utils.BASE_STORAGE;
        for (Map.Entry<UpgradeItem, Integer> entry : upgrades.entrySet()) {
            storage += entry.getKey().getData().getAdditionalStorageStacks() * entry.getValue();
        }
        return storage;
    }

    public ItemStack tryAddItem(ItemStack stack) {
        return barrelHandler.insertItem(0, stack, false);
    }

    public boolean canAcceptUpgrade(UpgradeData data) {
        return data.getSlotRequirement() <= getFreeSlots();
    }

    public void upgrade(UpgradeItem item) {
        int existing = upgrades.getOrDefault(item,0);
        if (existing == 0) {
            upgrades.put(item,1);
        } else {
            upgrades.put(item,existing + 1);
        }
        invalidateCaches();
        setChanged();
    }

    public int getTotalUpgradeSlots() {
        return ((BetterBarrelBlock)getBlockState().getBlock()).getBarrelTier().getUpgradeSlots();
    }


    public int getFreeSlots() {
        return getTotalUpgradeSlots() - getUsedSlots();
    }

    public static <T extends BlockEntity> void serverTick(Level pLevel1, BlockPos pPos, BlockState pState1, T pBlockEntity) {

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Stack", barrelHandler.getStack().save(new CompoundTag()));
        pTag.putInt("RealCount", barrelHandler.getStack().getCount());

        ListTag upgradesTag = new ListTag();

        for (Map.Entry<UpgradeItem, Integer> entry : upgrades.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("Item", Registry.ITEM.getKey(entry.getKey()).toString());
            tag.putInt("Count",entry.getValue());
            upgradesTag.add(tag);
        }
        pTag.put("Upgrades",upgradesTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ItemStack stack = ItemStack.of(pTag.getCompound("Stack"));
        stack.setCount(pTag.getInt("RealCount"));
        barrelHandler.setStack(stack);
        upgrades.clear();
        ListTag upgradesTag = pTag.getList("Upgrades", Tag.TAG_COMPOUND);
        for (Tag tag : upgradesTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            ResourceLocation name = new ResourceLocation(compoundTag.getString("Item"));
            upgrades.put((UpgradeItem)Registry.ITEM.get(name),compoundTag.getInt("Count"));
        }
        invalidateCaches();
    }

    public BarrelHandler getBarrelHandler() {
        return barrelHandler;
    }

    public ItemStack tryRemoveItem() {
        return getBarrelHandler().extractItem(0,64,false);
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

            int limit = getSlotLimit(slot);
            int count = stack.getCount();
            int existing = this.stack.isEmpty() ? 0 : this.stack.getCount();
            if (count + existing > limit) {
                if (!simulate) {
                    this.stack = ItemHandlerHelper.copyStackWithSize(stack, limit);
                    markDirty();
                }
                return ItemHandlerHelper.copyStackWithSize(stack, count + existing - limit);
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
            int existing = stack.getCount();
            ItemStack newStack;
            if (amount > existing) {
                newStack = ItemHandlerHelper.copyStackWithSize(stack, existing);
                if (!simulate) {
                    setStack(ItemStack.EMPTY);
                }
            } else {
                newStack = ItemHandlerHelper.copyStackWithSize(stack, amount);
                if (!simulate) {
                    stack.shrink(amount);
                }
            }
            if (!simulate) {
                markDirty();
            }
            return newStack;
        }
        @Override
        public int getSlotLimit(int slot) {
            return barrelBlockEntity.getStorage() * 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.stack.isEmpty() || ItemStack.isSameItemSameTags(this.stack, stack);
        }

        public void markDirty() {
            barrelBlockEntity.setChanged();
        }
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

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
