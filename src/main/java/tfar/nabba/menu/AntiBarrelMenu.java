package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.init.ModMenuTypes;

public class AntiBarrelMenu extends SearchableItemMenu<AntiBarrelBlockEntity.AntiBarrelInventory> {

    public AntiBarrelMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory, ContainerData data, ContainerData syncSlots) {
        this(ModMenuTypes.ANTI_BARREL, pContainerId, inventory, pAccess, antiBarrelInventory, data, syncSlots);
    }

    public AntiBarrelMenu(int i, Inventory inventory) {
        this(ModMenuTypes.ANTI_BARREL, i, inventory, ContainerLevelAccess.NULL, null, new SimpleContainerData(2), new SimpleContainerData(54));
    }

    protected AntiBarrelMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory, ContainerData
            data, ContainerData syncSlots) {
        super(pMenuType, pContainerId,inventory, access, antiBarrelInventory, data, syncSlots);
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

            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
            return ItemStack.EMPTY;
        }
        return itemstack;
    }
}
