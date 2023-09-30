package tfar.nabba.item.barrels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.capability.BetterBarrelItemStackItemHandler;
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
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BetterBarrelItemStackItemHandler(stack);
    }

    public static ItemStack getStoredItem(ItemStack barrel) {
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel) != null) {
            ItemStack stack = ItemStack.of(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound("Stack"));
            stack.setCount(BlockItemBarrelUtils.getBlockEntityTag(barrel).getInt("RealCount"));
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void setStack(ItemStack container, ItemStack copyStackWithSize) {
        CompoundTag tag = copyStackWithSize.save(new CompoundTag());
        BlockItemBarrelUtils.getOrCreateBlockEntityTag(container).put(NBTKeys.Stack.name(), tag);
        BlockItemBarrelUtils.getBlockEntityTag(container).putInt(NBTKeys.RealCount.name(), copyStackWithSize.getCount());
    }




    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStack disp = getStoredItem(stack);
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
        return getBooleanBlockStateValue(barrel, SingleSlotBarrelBlock.VOID);
    }
    public static boolean infiniteVending(ItemStack barrel) {
        return getBooleanBlockStateValue(barrel, SingleSlotBarrelBlock.INFINITE_VENDING);
    }

    public static boolean storageDowngrade(ItemStack barrel) {
        return getBooleanBlockStateValue(barrel,SingleSlotBarrelBlock.STORAGE_DOWNGRADE);
    }

    public static boolean getBooleanBlockStateValue(ItemStack barrel, BooleanProperty property) {
        if (getBlockStateTag(barrel)!=null) {
            CompoundTag blockStateTag = getBlockStateTag(barrel);
            blockStateTag.getBoolean(property.getName());
        }
        return false;
    }

    public static boolean isItemValid(ItemStack barrel,ItemStack stack) {
        if (!barrel.hasTag()) return true;
        ItemStack existing = getStoredItem(barrel);
        ItemStack ghost = getGhost(barrel);
        return CommonUtils.isItemValid(existing,stack,ghost);
    }

    public static ItemStack getGhost(ItemStack barrel) {
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel)!=null) {
            return ItemStack.of(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound(NBTKeys.Ghost.name()));
        }
        return ItemStack.EMPTY;
    }


}
