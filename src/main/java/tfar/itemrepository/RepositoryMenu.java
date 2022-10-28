package tfar.itemrepository;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.itemrepository.init.ModMenuTypes;
import tfar.itemrepository.inventory.RepositorySlot;
import tfar.itemrepository.world.RepositoryInventory;

import javax.annotation.Nonnull;

public class RepositoryMenu extends AbstractContainerMenu {

    private final RepositoryInventory repositoryInventory;

    protected RepositoryMenu(int pContainerId, Inventory inventory, RepositoryInventory repositoryInventory) {
        this(ModMenuTypes.REPOSITORY, pContainerId, inventory, repositoryInventory);
    }

    public RepositoryMenu(int i, Inventory inventory) {
        this(ModMenuTypes.REPOSITORY, i, inventory, new RepositoryInventory());
    }

    protected RepositoryMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, RepositoryInventory repositoryInventory) {
        super(pMenuType, pContainerId);
        this.repositoryInventory = repositoryInventory;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new RepositorySlot(repositoryInventory, x + 6 * y, 8 + 18 * x, 18 + 18 * y, this));
            }
        }

        int playerX = 8;
        int playerY = 140;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + playerX, i * 18 + playerY));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, i * 18 + playerX, playerY + 58));
        }
    }


    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {

        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack ret = slot.getItem().copy();
        ItemStack stack = slot.getItem().copy();

        boolean nothingDone;

        //is this the crafting output slot?
        if (index < 54) {
            // Try moving module -> player inventory
            nothingDone = !moveToPlayerInventory((RepositorySlot) slot, stack);

            // Try moving module -> tile inventory
        }
        // Is the slot from the tile?
        else {
            // try moving player -> modules
            nothingDone = !moveToRepository(stack);
        }

        if (nothingDone) {
            return ItemStack.EMPTY;
        }
        return notifySlotAfterTransfer(playerIn, stack, ret, slot);
    }

    @Nonnull
    protected ItemStack notifySlotAfterTransfer(Player player, @Nonnull ItemStack stack, @Nonnull ItemStack original, Slot slot) {
        // notify slot

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        // update slot we pulled from
        slot.set(stack);
        slot.onTake(player, stack);

        if (slot.hasItem() && slot.getItem().isEmpty()) {
            slot.set(ItemStack.EMPTY);
        }

        return original;
    }

    protected boolean moveToPlayerInventory(RepositorySlot slot, @Nonnull ItemStack itemstack) {
        for (int i = getSlotCount(); i < slots.size(); i++) {
            Slot targetSlot = slots.get(i);
            if (!targetSlot.hasItem()) {
                targetSlot.set(itemstack.copy());
                itemstack.setCount(0);
                slot.removeItem();
                return true;
            }
        }
        return false;
    }

    protected boolean moveToRepository(@Nonnull ItemStack itemstack) {
        if (itemstack.getMaxStackSize() != 1) {
            return false;
        } else {
            repositoryInventory.addItem(itemstack.copy());
            itemstack.setCount(0);
            return true;
        }
    }

    public int getRows() {
        return (int) Math.ceil((double) getSlotCount() / 6);
    }

    public int getSlotCount() {
        return 54;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
