package tfar.nabba.api;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;

public interface DisplayType {

AbstractContainerMenu createBarrelInterfaceMenu(int pContainerId, Inventory pPlayerInventory, BarrelInterfaceBlockEntity barrelInterfaceBlockEntity);
AbstractContainerMenu createControllerMenu(int pContainerId, Inventory pPlayerInventory, ControllerBlockEntity controllerBlockEntity);

}
