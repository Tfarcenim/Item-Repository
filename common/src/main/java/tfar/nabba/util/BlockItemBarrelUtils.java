package tfar.nabba.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class BlockItemBarrelUtils {
    public static CompoundTag getBlockStateTag(ItemStack barrel) {
        return barrel.getTagElement(BlockItem.BLOCK_STATE_TAG);
    }

    public static CompoundTag getOrCreateBlockEntityTag(ItemStack barrel) {
        return barrel.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
    }

    @Nullable
    public static CompoundTag getBlockEntityTag(ItemStack barrel) {
        return barrel.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
    }

    public static void setStack(ItemStack container, ItemStack copyStackWithSize) {
        CompoundTag tag = copyStackWithSize.save(new CompoundTag());
        getOrCreateBlockEntityTag(container).put(NBTKeys.Stack.name(), tag);
        getBlockEntityTag(container).putInt(NBTKeys.RealCount.name(), copyStackWithSize.getCount());
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
        ItemStack ghost = getItemGhost(barrel);
        return CommonUtils.isItemValid(existing,stack,ghost);
    }

    public static ItemStack getItemGhost(ItemStack barrel) {
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel)!=null) {
            return ItemStack.of(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound(NBTKeys.Ghost.name()));
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getStoredItem(ItemStack barrel) {
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel) != null) {
            ItemStack stack = ItemStack.of(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound("Stack"));
            stack.setCount(BlockItemBarrelUtils.getBlockEntityTag(barrel).getInt("RealCount"));
            return stack;
        }
        return ItemStack.EMPTY;
    }

}
