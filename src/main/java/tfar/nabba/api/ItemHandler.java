package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

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

}
