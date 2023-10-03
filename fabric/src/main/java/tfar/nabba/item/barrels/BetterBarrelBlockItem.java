package tfar.nabba.item.barrels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.util.BlockItemBarrelUtils;
import tfar.nabba.util.CommonUtils;
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

}
