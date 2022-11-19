package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.S2CRefreshClientStacksPacket;
import tfar.nabba.util.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchableItemMenu<T extends SearchableItemHandler> extends SearchableMenu {

    public final T itemHandler;

    protected SearchableItemMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, T itemHandler, ContainerData inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId,inventory,access,inventoryData,syncSlots);
        this.itemHandler = itemHandler;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }



    public void refreshDisplay(ServerPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        List<Integer> syncSlots = itemHandler.getDisplaySlots(getRowSlot().get(),getAccess().evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HasSearchBar repositoryBlock) {
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

    public void handleItemExtract(ServerPlayer player, int slot, int amount, boolean shift) {

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
