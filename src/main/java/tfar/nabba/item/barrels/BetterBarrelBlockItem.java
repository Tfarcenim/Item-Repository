package tfar.nabba.item.barrels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.capability.BetterBarrelItemStackItemHandler;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BetterBarrelBlockItem extends BlockItem {
    public BetterBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public static void setStack(ItemStack container, ItemStack copyStackWithSize) {
        CompoundTag tag = copyStackWithSize.save(new CompoundTag());
        getOrCreateBlockEntityTag(container).put(NBTKeys.Stack.name(), tag);
        getBlockEntityTag(container).putInt(NBTKeys.RealCount.name(), copyStackWithSize.getCount());
    }

    @Nullable
    public static CompoundTag getBlockEntityTag(ItemStack barrel) {
        return barrel.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
    }

    public static CompoundTag getBlockStateTag(ItemStack barrel) {
        return barrel.getTagElement(BlockItem.BLOCK_STATE_TAG);
    }

    public static CompoundTag getOrCreateBlockEntityTag(ItemStack barrel) {
        return barrel.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStack disp = getStoredItem(stack);
        return disp.isEmpty() ? super.getTooltipImage(stack) : Optional.of(new BetterBarrelTooltip(disp));
    }

    public static ItemStack getStoredItem(ItemStack barrel) {
        if (getBlockEntityTag(barrel) != null) {
            ItemStack stack = ItemStack.of(getBlockEntityTag(barrel).getCompound("Stack"));
            stack.setCount(getBlockEntityTag(barrel).getInt("RealCount"));
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static List<UpgradeStack> getUpgrades(ItemStack barrel) {
        List<UpgradeStack> upgradeStacks = new ArrayList<>();
        if (getBlockEntityTag(barrel)!=null) {
            CompoundTag blockEntityTag = getBlockEntityTag(barrel);
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

    public static int getStorageUnits(ItemStack barrel,BarrelType type) {
        int storage = Utils.BASE_STORAGE.get(type);
        List<UpgradeStack> upgrades = getUpgrades(barrel);
        for (UpgradeStack upgradeStack : upgrades) {
            storage += upgradeStack.getStorageUnits(type);
        }
        return storage;
    }

    public static boolean isVoid(ItemStack barrel) {
        if (!barrel.hasTag())return false;
        if (getBlockStateTag(barrel)!= null) {
            return getBlockStateTag(barrel).getBoolean("void");
        }
        return false;
    }

    public static boolean infiniteVending(ItemStack barrel) {
        if (getBlockEntityTag(barrel)!=null) {
            CompoundTag blockEntityTag = getBlockEntityTag(barrel);
            ListTag listTag = blockEntityTag.getList(NBTKeys.Upgrades.name(), Tag.TAG_COMPOUND);

        }
        return false;
    }

    public static boolean isItemValid(ItemStack barrel,ItemStack stack) {
        if (!barrel.hasTag()) return true;
        ItemStack existing = getStoredItem(barrel);
        ItemStack ghost = getGhost(barrel);
        return Utils.isItemValid(existing,stack,ghost);
    }

    public static ItemStack getGhost(ItemStack barrel) {
        if (getBlockEntityTag(barrel)!=null) {
            return ItemStack.of(getBlockEntityTag(barrel).getCompound(NBTKeys.Ghost.name()));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BetterBarrelItemStackItemHandler(stack);
    }
}
