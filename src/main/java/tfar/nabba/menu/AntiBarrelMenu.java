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
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.S2CRefreshClientStacksPacket;

import java.util.ArrayList;
import java.util.List;

public class AntiBarrelMenu extends SearchableMenu<AntiBarrelBlockEntity.AntiBarrelInventory> {

    public AntiBarrelMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory, ContainerData data, ContainerData syncSlots) {
        this(ModMenuTypes.ANTI_BARREL, pContainerId, inventory, pAccess, antiBarrelInventory, data, syncSlots);
    }

    public AntiBarrelMenu(int i, Inventory inventory) {
        this(ModMenuTypes.ANTI_BARREL, i, inventory, ContainerLevelAccess.NULL, null, new SimpleContainerData(2), new SimpleContainerData(54));
    }

    protected AntiBarrelMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory, ContainerData
            data, ContainerData syncSlots) {
        super(pMenuType, pContainerId, access, antiBarrelInventory, data, syncSlots);
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

            if (!itemHandler.isItemValid(0, itemstack1) || itemHandler.isFull()) {
                return ItemStack.EMPTY;
            }

            itemHandler.universalAddItem(itemstack1, false);
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
}
