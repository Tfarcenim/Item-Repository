package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public interface ItemHandler extends IItemHandler {

    boolean isFull();

    default ItemStack storeItem(ItemStack stack, boolean simulate) {
        if (isFull()) return stack;
        ItemStack remainder = stack;

        for (int i = 0; i < getSlots();i++) {
            remainder = insertItem(i,remainder,simulate);
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return remainder;
    }

    default ItemStack requestItem(ItemStack stack) {
        ItemStack remaining = ItemHandlerHelper.copyStackWithSize(stack,stack.getMaxStackSize());
        ItemStack extracted = ItemStack.EMPTY;
        for (int i = 0; i < getSlots();i++) {
            ItemStack simExtract = extractItem(i,remaining.getCount(),true);
            if (!simExtract.isEmpty() && ItemStack.isSameItemSameTags(simExtract,remaining)) {
                extractItem(i,remaining.getCount(),false);
                if (extracted.isEmpty()) {
                    extracted = simExtract;
                } else {
                    extracted.grow(simExtract.getCount());
                }
                remaining.shrink(simExtract.getCount());
                if (remaining.isEmpty()) {
                    return extracted;
                }
            }
        }
        return extracted;
    }
}
