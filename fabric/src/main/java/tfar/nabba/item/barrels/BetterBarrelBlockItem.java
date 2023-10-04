package tfar.nabba.item.barrels;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import tfar.nabba.NABBAFabric;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.util.BlockItemBarrelUtils;
import tfar.nabba.util.NBTKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BetterBarrelBlockItem extends BlockItem {
    public BetterBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }


    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStack disp = BlockItemBarrelUtils.getStoredItem(stack);
        return disp.isEmpty() ? super.getTooltipImage(stack) : Optional.of(new BetterBarrelTooltip(disp));
    }

    public static List<UpgradeStack> getUpgrades(ItemStack barrel) {
        List<UpgradeStack> upgradeStacks = new ArrayList<>();
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel)!=null) {
            CompoundTag blockEntityTag = BlockItemBarrelUtils.getBlockEntityTag(barrel);
            ListTag listTag = blockEntityTag.getList(NBTKeys.Upgrades.name(), Tag.TAG_COMPOUND);
            for (Tag tag : listTag) {
                upgradeStacks.add(UpgradeStack.of((CompoundTag) tag));
            }
        }
        return upgradeStacks;
    }

    public static int getUsedSlotsFromItem(ItemStack barrel) {
        int i = 0;
        for (UpgradeStack stack : getUpgrades(barrel)) {
            i += stack.getUpgradeSlotsRequired();
        }
        return i;
    }

    public static int getStorageMultiplier(ItemStack barrel) {
        int storage = 1;
        List<UpgradeStack> upgrades = getUpgrades(barrel);
        for (UpgradeStack upgradeStack : upgrades) {
            storage += upgradeStack.getStorageMultiplier();
        }
        return storage;
    }

    public static boolean isVoid(ItemStack barrel) {
        return BlockItemBarrelUtils.getBooleanBlockStateValue(barrel, SingleSlotBarrelBlock.VOID);
    }
    public static boolean infiniteVending(ItemStack barrel) {
        return BlockItemBarrelUtils.getBooleanBlockStateValue(barrel, SingleSlotBarrelBlock.INFINITE_VENDING);
    }

    public static boolean storageDowngrade(ItemStack barrel) {
        return BlockItemBarrelUtils.getBooleanBlockStateValue(barrel,SingleSlotBarrelBlock.STORAGE_DOWNGRADE);
    }

    public static SingleItemBarrelWrapper getStorage(ItemStack stack) {
        if (stack.getItem() instanceof BetterBarrelBlockItem) {
            return new SingleItemBarrelWrapper(stack);
        }
        return null;
    }



    public static class SingleItemBarrelWrapper extends SingleStackStorage {

        private final ItemStack barrel;

        private ItemStack lastReleasedSnapshot = null;


        public SingleItemBarrelWrapper(ItemStack barrel) {
            this.barrel = barrel;
        }

        @Override
        protected ItemStack getStack() {
            return BlockItemBarrelUtils.getStoredItem(barrel);
        }

        @Override
        protected void setStack(ItemStack stack) {
            BlockItemBarrelUtils.setStack(barrel,stack);
        }

        @Override
        protected int getCapacity(ItemVariant variant) {
            ItemStack existing = BlockItemBarrelUtils.getStoredItem(barrel);
            return BetterBarrelBlockItem.getStorageMultiplier(barrel) * existing.getMaxStackSize() *
                    (BetterBarrelBlockItem.storageDowngrade(barrel) ? 1 : NABBAFabric.ServerCfg.better_barrel_base_storage);
        }


//        @Override
 //       public void updateSnapshots(TransactionContext transaction) {
 //           storage.setChanged();
 //           super.updateSnapshots(transaction);
 //       }

        @Override
        protected void releaseSnapshot(ItemStack snapshot) {
            lastReleasedSnapshot = snapshot;
        }

        @Override
        protected void onFinalCommit() {
            // Try to apply the change to the original stack
            ItemStack original = lastReleasedSnapshot;
            ItemStack currentStack = getStack();

            if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
                // None is empty and the items match: just update the amount and NBT, and reuse the original stack.
                original.setCount(currentStack.getCount());
                original.setTag(currentStack.hasTag() ? currentStack.getTag().copy() : null);
                setStack(original);
            } else {
                // Otherwise assume everything was taken from original so empty it.
                original.setCount(0);
            }
        }
    }
}
