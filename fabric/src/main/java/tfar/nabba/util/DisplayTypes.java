package tfar.nabba.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.apache.commons.lang3.function.TriFunction;
import tfar.nabba.api.DisplayType;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.menu.ControllerKeyFluidMenu;
import tfar.nabba.menu.ControllerKeyItemMenu;

public enum DisplayTypes implements DisplayType {
    ITEM(
            (containerId, inventory, barrelInterfaceBlockEntity) -> new ControllerKeyItemMenu(containerId, inventory, ContainerLevelAccess.create(barrelInterfaceBlockEntity.getLevel(), barrelInterfaceBlockEntity.getBlockPos()), barrelInterfaceBlockEntity.getWrapper(),
            barrelInterfaceBlockEntity.getItemDataAccess(), barrelInterfaceBlockEntity.getSyncSlotsAccess()),

            (containerId, inventory, controllerBlockEntity) -> new ControllerKeyItemMenu(containerId, inventory, ContainerLevelAccess.create(controllerBlockEntity.getLevel(), controllerBlockEntity.getBlockPos()), controllerBlockEntity.getHandler(),
            controllerBlockEntity.getItemDataAccess(), controllerBlockEntity.getSyncSlotsAccess())
    ),
    FLUID(
            (containerId, inventory, barrelInterfaceBlockEntity) -> new ControllerKeyFluidMenu(containerId, inventory, ContainerLevelAccess.create(barrelInterfaceBlockEntity.getLevel(), barrelInterfaceBlockEntity.getBlockPos()), barrelInterfaceBlockEntity.getWrapper(),
            barrelInterfaceBlockEntity.getFluidDataAccess(), barrelInterfaceBlockEntity.getSyncSlotsAccess()),
            (containerId, inventory, barrelInterfaceBlockEntity) -> new ControllerKeyFluidMenu(containerId, inventory, ContainerLevelAccess.create(barrelInterfaceBlockEntity.getLevel(), barrelInterfaceBlockEntity.getBlockPos()), barrelInterfaceBlockEntity.getHandler(),
                    barrelInterfaceBlockEntity.getFluidDataAccess(), barrelInterfaceBlockEntity.getSyncSlotsAccess())

    );

    private final TriFunction<Integer, Inventory, BarrelInterfaceBlockEntity, AbstractContainerMenu> barrelInterfaceFunction;
    private final TriFunction<Integer, Inventory, ControllerBlockEntity, AbstractContainerMenu> controllerFunction;

    DisplayTypes(TriFunction<Integer,Inventory,BarrelInterfaceBlockEntity,AbstractContainerMenu> barrelInterfaceFunction,TriFunction<Integer,Inventory,ControllerBlockEntity,AbstractContainerMenu> controllerFunction) {
        this.barrelInterfaceFunction = barrelInterfaceFunction;
        this.controllerFunction = controllerFunction;
    }

    @Override
    public AbstractContainerMenu createBarrelInterfaceMenu(int pContainerId, Inventory pPlayerInventory, BarrelInterfaceBlockEntity barrelInterfaceBlockEntity) {
        return barrelInterfaceFunction.apply(pContainerId,pPlayerInventory,barrelInterfaceBlockEntity);
    }

    @Override
    public AbstractContainerMenu createControllerMenu(int pContainerId, Inventory pPlayerInventory, ControllerBlockEntity controllerBlockEntity) {
        return controllerFunction.apply(pContainerId,pPlayerInventory,controllerBlockEntity);
    }
}
