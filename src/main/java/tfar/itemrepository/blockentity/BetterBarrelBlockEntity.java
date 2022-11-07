package tfar.itemrepository.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.itemrepository.block.BetterBarrelBlock;
import tfar.itemrepository.init.ModBlockEntityTypes;
import tfar.itemrepository.item.UpgradeItem;
import tfar.itemrepository.util.BarrelTier;
import tfar.itemrepository.util.UpgradeData;
import tfar.itemrepository.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class BetterBarrelBlockEntity extends BlockEntity {

    private Map<UpgradeItem,Integer> upgrades = new HashMap<>();
    private transient int cachedStorage = Utils.INVALID;
    public BetterBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        barrelHandler = new BarrelHandler(this);
    }

    private final BarrelHandler barrelHandler;
    public BetterBarrelBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.REPOSITORY,pos,state);
    }



    public int getStorage() {
        if (cachedStorage == -1) {//save CPU cycles by not iterating the upgrade map
            cachedStorage = computeStorage();
        }
        return cachedStorage;
    }

    private int computeStorage() {
        int storage = Utils.BASE_STORAGE;
        for (Map.Entry<UpgradeItem,Integer> entry: upgrades.entrySet()) {
            storage += entry.getKey().getData().getAdditionalStorageStacks() * entry.getValue();
        }
        return storage;
    }

    public ItemStack tryAddItem(ItemStack stack) {
        return barrelHandler.insertItem(0,stack,false);
    }

    public boolean canAcceptUpgrade(UpgradeData data) {
        BarrelTier barrelTier = ((BetterBarrelBlock)getBlockState().getBlock()).getBarrelTier();
        return true;
    }

    public static <T extends BlockEntity> void serverTick(Level pLevel1, BlockPos pPos, BlockState pState1, T pBlockEntity) {

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Stack",barrelHandler.getStack().save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ItemStack stack = ItemStack.of(pTag.getCompound("Stack"));
        barrelHandler.stack = stack;
    }

    public BarrelHandler getBarrelHandler() {
        return barrelHandler;
    }

    public static class BarrelHandler implements IItemHandler {
        private final BetterBarrelBlockEntity barrelBlockEntity;

        BarrelHandler(BetterBarrelBlockEntity barrelBlockEntity)  {
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

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return stack;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()|| !(!this.stack.isEmpty() && !ItemStack.isSameItemSameTags(this.stack,stack))) return stack;
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return barrelBlockEntity.getStorage() * 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.isEmpty() || stack.getItem() == this.stack.getItem();
        }
    }
}
