package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;

public interface HasItemHandler extends HasHandler {
    ItemHandler getItemHandler();

    default ItemStack tryAddItem(ItemStack stack) {
        return getItemHandler().insertItem(0, stack, false);
    }

    default boolean isFull() {
        return getItemHandler().isFull();
    }
}
