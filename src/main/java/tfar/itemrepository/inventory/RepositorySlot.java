package tfar.itemrepository.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.itemrepository.RepositoryMenu;
import tfar.itemrepository.world.RepositoryInventory;

public class RepositorySlot extends SlotItemHandler {
    private final int index;
    private final RepositoryMenu menu;

    public RepositorySlot(RepositoryInventory itemHandler, int index, int xPosition, int yPosition, RepositoryMenu menu) {
        super(itemHandler, index, xPosition, yPosition);
        this.index = index;
        this.menu = menu;
    }

    //called on client to set stacks sent from server
    @Override
    public void initialize(ItemStack stack) {
        this.getItemHandler().addItem(stack);
        this.setChanged();
    }

    //called on both client and server when adding an item
    @Override
    public void set(@NotNull ItemStack stack) {
        if (!stack.isEmpty()) {
            this.getItemHandler().addItem(stack);
        }
        this.setChanged();
    }

    public void removeItem() {
        remove(1);
    }

    @Override
    public RepositoryInventory getItemHandler() {
        return (RepositoryInventory) super.getItemHandler();
    }
}
