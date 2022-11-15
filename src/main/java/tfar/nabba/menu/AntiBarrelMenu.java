package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.S2CRefreshClientStacksPacket;

import java.util.ArrayList;
import java.util.List;

public class AntiBarrelMenu extends AbstractContainerMenu {

    public final AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory;

    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final ContainerData syncSlots;

    private final DataSlot row = DataSlot.standalone();

    public AntiBarrelMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory, ContainerData data, ContainerData syncSlots) {
        this(ModMenuTypes.ANTI_BARREL, pContainerId, inventory,pAccess, antiBarrelInventory,data,syncSlots);
    }

    public AntiBarrelMenu(int i, Inventory inventory) {
        this(ModMenuTypes.ANTI_BARREL, i, inventory,ContainerLevelAccess.NULL, null,new SimpleContainerData(2),new SimpleContainerData(54));
    }

    protected AntiBarrelMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory, ContainerData
                             data, ContainerData syncSlots) {
        super(pMenuType, pContainerId);
        this.access = access;
        this.data = data;
        this.syncSlots = syncSlots;
        this.antiBarrelInventory = antiBarrelInventory;

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
        addDataSlots(data);
        addDataSlots(syncSlots);

        addDataSlot(row);
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

            if (!antiBarrelInventory.isItemValid(0,itemstack1)|| antiBarrelInventory.isFull()) {
                return ItemStack.EMPTY;
            }

            antiBarrelInventory.addItem(itemstack1);
            ItemStack stack = ItemStack.EMPTY;
            slot.set(stack);
            broadcastChanges();
            if (playerIn instanceof ServerPlayer sp) {
                refreshDisplay(sp);
            }
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
            return ItemStack.EMPTY;
        }
        return itemstack;
    }

    public int getTotalRows() {
        return (int) Math.ceil((double) getTotalSlotCount() / 9);
    }
    public int getSearchRows() {
        return (int) Math.ceil((double) getSearchSlotCount() / 9);
    }

    public int getCurrentRow() {
        return row.get();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public int getTotalSlotCount() {
        return data.get(0);
    }

    public int getSearchSlotCount() {
        return data.get(1);
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

    public void handleRequest(ServerPlayer player, int slot, int amount, boolean shift) {

        if (!antiBarrelInventory.isSlotValid(slot)) {
            return;
        }

        if (shift) {
            ItemHandlerHelper.giveItemToPlayer(player, antiBarrelInventory.getStackInSlot(slot).copy());
        } else {
            setCarried(antiBarrelInventory.getStackInSlot(slot).copy());
        }

        antiBarrelInventory.extractItem(slot,amount,false);

        refreshDisplay(player);
    }

    public void refreshDisplay(ServerPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        List<Integer> syncSlots = antiBarrelInventory.getDisplaySlots(row.get(),access.evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AntiBarrelBlockEntity repositoryBlock) {
                return repositoryBlock.search;
            }
            return "";
        },""));
        for (int i = 0; i < syncSlots.size();i++) {
            list.add(antiBarrelInventory.getStackInSlot(syncSlots.get(i)));
        }
        PacketHandler.sendToClient(new S2CRefreshClientStacksPacket(list,syncSlots), player);
    }

    public int getDisplaySlot(int slot) {
        return syncSlots.get(slot);
    }

    public void handleInsert(ServerPlayer player) {
        if (!antiBarrelInventory.isFull()) {
            antiBarrelInventory.addItem(getCarried().copy());
            setCarried(ItemStack.EMPTY);
            refreshDisplay(player);
        }
    }

    public void handleSearch(ServerPlayer player, String search) {
        access.execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AntiBarrelBlockEntity repositoryBlock) {
                repositoryBlock.search = search;
            }
        });
        refreshDisplay(player);
    }
}
