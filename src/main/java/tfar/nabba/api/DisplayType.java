package tfar.nabba.api;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;

public interface DisplayType {

AbstractContainerMenu createBarrelInterfaceMenu(int pContainerId, Inventory pPlayerInventory, BarrelInterfaceBlockEntity barrelInterfaceBlockEntity);
}
