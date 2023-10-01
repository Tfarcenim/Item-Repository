package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import tfar.nabba.shim.IItemHandlerShim;

public interface IItemHandlerItem extends IItemHandlerShim {
    ItemStack getContainer();
}
