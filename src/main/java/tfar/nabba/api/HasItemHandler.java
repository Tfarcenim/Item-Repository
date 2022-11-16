package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface HasItemHandler {
    IItemHandler getItemHandler();

    default ItemStack tryAddItem(ItemStack stack) {
        return getItemHandler().insertItem(0, stack, false);
    }
}
