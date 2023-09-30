package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public interface IItemHandlerItem extends IItemHandler {
    ItemStack getContainer();
}
