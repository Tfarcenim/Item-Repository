package tfar.nabba.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.CommonUtils;
import tfar.nabba.util.ItemStackUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public abstract class SearchableItemMenu<T extends SearchableItemHandler> extends SearchableMenu<ItemStack> {

    public final T itemHandler;


    protected SearchableItemMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, T itemHandler, ContainerData inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId,inventory,access,inventoryData,syncSlots);
        this.itemHandler = itemHandler;
    }

    public void refreshDisplay(ServerPlayer player, boolean forced) {
        List<ItemStack> list = getDisplaySlots();

        boolean changed = forced || list.size() != remoteStacks.size();

        if (!changed) {
            for (int i = 0; i < list.size(); i++) {
                if (!ItemStack.matches(remoteStacks.get(i), list.get(i))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            remoteStacks.clear();
            remoteStacks.addAll(list);
            PacketHandler.sendToClient(player, PacketHandler.refresh_items, new ListSerializer(list));
        }
    }

    public static class ListSerializer implements Consumer<FriendlyByteBuf> {
        private final List<ItemStack> list;
        public ListSerializer(List<ItemStack> list) {
            this.list = list;
        }

        @Override
        public void accept(FriendlyByteBuf buf) {
            buf.writeInt(list.size());
            IntStream.range(0,list.size()).forEach(integer -> ItemStackUtil.writeExtendedItemStack(buf,list.get(integer)));
        }
    }

    @Override
    public List<ItemStack> getDisplaySlots() {
        return itemHandler.getItemsForDisplay(getRowSlot().get(),getAccess().evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HasSearchBar repositoryBlock) {
                return repositoryBlock.getSearchString();
            }
            return "";
        },""));
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int slotIndex) {
        if (playerIn.level().isClientSide) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();

            ItemStack rejected = itemHandler.storeItem(stack,false);
            if (rejected != stack) {
                slot.set(rejected);
                slot.onTake(playerIn, stack);
            }
        }
        return ItemStack.EMPTY;
    }
    public T getItemHandler() {
        return itemHandler;
    }

    public void handleInsert(ServerPlayer player, int count) {
        ItemStack carried = getCarried();

        ItemStack store = carried.copy();
        store.setCount(count);
        ItemStack reject = itemHandler.storeItem(store,false);
        int diff = store.getCount() - reject.getCount();
        //todo: does this break things
        setCarried(CommonUtils.copyStackWithSize(carried,carried.getCount() - diff));
    }

    public void handleItemExtract(ServerPlayer player, ItemStack stack, boolean shift) {
        ItemStack received = itemHandler.requestItem(stack);
        if (shift) {
            CommonUtils.giveItemToPlayer(player, received);
        } else {
            setCarried(received);
        }
    }
}
