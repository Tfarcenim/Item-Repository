package tfar.itemrepository;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tfar.itemrepository.init.ModMenuTypes;
import tfar.itemrepository.net.PacketHandler;
import tfar.itemrepository.net.S2CRefreshClientStacksPacket;
import tfar.itemrepository.world.RepositoryInventory;

import java.util.List;

public class RepositoryMenu extends AbstractContainerMenu {

    public final RepositoryInventory repositoryInventory;

    private final ContainerData data;

    private int row;
    public final int[] displaySlots = new int[54];



    protected RepositoryMenu(int pContainerId, Inventory inventory, RepositoryInventory repositoryInventory, ContainerData data) {
        this(ModMenuTypes.REPOSITORY, pContainerId, inventory, repositoryInventory,data);
    }

    public RepositoryMenu(int i, Inventory inventory) {
        this(ModMenuTypes.REPOSITORY, i, inventory, null,new SimpleContainerData(1));
    }

    protected RepositoryMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, RepositoryInventory repositoryInventory,ContainerData
                             data) {
        super(pMenuType, pContainerId);
        this.data = data;
        this.repositoryInventory = repositoryInventory;

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
        this.addDataSlots(data);
        defaultDisplaySlots();
    }

    private void defaultDisplaySlots() {
        for (int i = 0; i < displaySlots.length;i++) {
            displaySlots[i] = i;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int slotIndex) {
        if (playerIn.level.isClientSide) {
            return ItemStack.EMPTY;
        }
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            if (!repositoryInventory.isItemValid(0,itemstack1)) {
                return ItemStack.EMPTY;
            }

            repositoryInventory.addItem(itemstack1);
            ItemStack stack = ItemStack.EMPTY;
            slot.set(stack);
            broadcastChanges();
            List<ItemStack> list = repositoryInventory.getStacks();
            if (playerIn instanceof ServerPlayer sp) {
                PacketHandler.sendToClient(new S2CRefreshClientStacksPacket(list), sp);
            }
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
            return ItemStack.EMPTY;
        }
        return itemstack;
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

    public int getRealSlots() {
        return data.get(0);
    }

    public void handleScroll(int scroll_amount) {
    }

    public void handleRequest(ServerPlayer player, int slot, int amount, boolean shift) {

        if (!repositoryInventory.isSlotValid(slot)) {
            return;
        }

        if (shift) {
            ItemHandlerHelper.giveItemToPlayer(player,repositoryInventory.getStackInSlot(slot).copy());
        } else {
            setCarried(repositoryInventory.getStackInSlot(slot).copy());
        }

        repositoryInventory.extractItem(slot,amount,false);

        refreshDisplay(player);
    }

    public void refreshDisplay(ServerPlayer player) {
        List<ItemStack> list = repositoryInventory.getDisplayStacks(displaySlots);
        PacketHandler.sendToClient(new S2CRefreshClientStacksPacket(list), player);
    }

    public void handleInsert(ServerPlayer player) {
        repositoryInventory.addItem(getCarried().copy());
        setCarried(ItemStack.EMPTY);
        refreshDisplay(player);
    }
}
