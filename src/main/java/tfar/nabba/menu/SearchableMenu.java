package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.ItemHandler;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.S2CRefreshClientStacksPacket;
import tfar.nabba.util.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchableMenu<T extends SearchableItemHandler> extends AbstractContainerMenu {

    private final DataSlot row = DataSlot.standalone();
    private final ContainerLevelAccess access;

    private final ContainerData syncSlots;
    private final ContainerData inventoryData;

    public final T itemHandler;


    protected SearchableMenu(@Nullable MenuType<?> pMenuType, int pContainerId, ContainerLevelAccess access,T itemHandler,ContainerData inventoryData,ContainerData syncSlots) {
        super(pMenuType, pContainerId);
        addDataSlot(row);
        this.access = access;
        this.syncSlots = syncSlots;
        this.itemHandler = itemHandler;
        this.inventoryData = inventoryData;
        addDataSlots(inventoryData);
        addDataSlots(syncSlots);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public DataSlot getRowSlot() {
        return row;
    }

    public ContainerLevelAccess getAccess() {
        return access;
    }

    public void refreshDisplay(ServerPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        List<Integer> syncSlots = itemHandler.getDisplaySlots(row.get(),access.evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ControllerBlockEntity repositoryBlock) {
                return repositoryBlock.getSearchString();
            }
            return "";
        },""));
        for (int i = 0; i < syncSlots.size();i++) {
            list.add(itemHandler.getStackInSlot(syncSlots.get(i)));
        }
        PacketHandler.sendToClient(new S2CRefreshClientStacksPacket(list,syncSlots), player);
    }


    @Override
    public ItemStack quickMoveStack(Player playerIn, int slotIndex) {
        if (playerIn.level.isClientSide) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();

            ItemStack rejected = itemHandler.universalAddItem(stack,false);
            slot.set(rejected);
            slot.onTake(playerIn,stack);
            broadcastChanges();
            if (playerIn instanceof ServerPlayer sp) {
                refreshDisplay(sp);
            }
        }
        return ItemStack.EMPTY;
    }

    public void handleScroll(ServerPlayer player,int scroll_amount) {
        int rows = getSearchRows();
        if (rows > 6) {
            if (scroll_amount < 0 && row.get() < rows - 6) {
                row.set(row.get() + 1);
            } else if (scroll_amount > 0 && row.get() > 0) {
                row.set(row.get() - 1);
            }
        }
        refreshDisplay(player);
    }

    public int getSearchRows() {
        return (int) Math.ceil((double) getSearchSlotCount() / 9);
    }

    public int getCurrentRow() {
        return getRowSlot().get();
    }

    public int getFilledSlotCount() {
        return inventoryData.get(0);
    }

    public int getSearchSlotCount() {
        return inventoryData.get(1);
    }
    public int getDisplaySlot(int slot) {
        return syncSlots.get(slot);
    }

    public T getItemHandler() {
        return itemHandler;
    }


    public void handleInsert(ServerPlayer player, int slot) {
        ItemStack carried = getCarried();

        if (slot == Utils.INVALID || slot >= itemHandler.getSlots()) {
            ItemStack reject = itemHandler.universalAddItem(carried,false);
            setCarried(reject);
        } else {
            ItemStack reject = itemHandler.insertItem(slot,carried,false);
            setCarried(reject);
        }
        refreshDisplay(player);
    }

    public void handleSearch(ServerPlayer player, String search) {
        access.execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HasSearchBar repositoryBlock) {
                repositoryBlock.setSearchString(search);
            }
        });
        refreshDisplay(player);
    }


    public void handleRequest(ServerPlayer player, int slot, int amount, boolean shift) {

        //if (!antiBarrelInventory.isSlotValid(slot)) {
        //   return;
        //  }

        ItemStack stack = itemHandler.extractItem(slot,amount,false);

        if (shift) {
            ItemHandlerHelper.giveItemToPlayer(player,stack);
        } else {
            setCarried(stack);
        }


        refreshDisplay(player);
    }
}
