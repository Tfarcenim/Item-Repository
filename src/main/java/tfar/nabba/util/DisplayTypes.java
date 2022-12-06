package tfar.nabba.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.apache.commons.lang3.function.TriFunction;
import tfar.nabba.api.DisplayType;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;
import tfar.nabba.menu.ControllerKeyFluidMenu;
import tfar.nabba.menu.ControllerKeyItemMenu;

public enum DisplayTypes implements DisplayType {
    ITEM((containerId, inventory, barrelInterfaceBlockEntity) -> new ControllerKeyItemMenu(containerId, inventory, ContainerLevelAccess.create(barrelInterfaceBlockEntity.getLevel(), barrelInterfaceBlockEntity.getBlockPos()), barrelInterfaceBlockEntity.getWrapper(),
            barrelInterfaceBlockEntity.getItemDataAccess(), barrelInterfaceBlockEntity.getSyncSlotsAccess())),
    FLUID((containerId, inventory, barrelInterfaceBlockEntity) -> new ControllerKeyFluidMenu(containerId, inventory, ContainerLevelAccess.create(barrelInterfaceBlockEntity.getLevel(), barrelInterfaceBlockEntity.getBlockPos()), barrelInterfaceBlockEntity.getWrapper(),
            barrelInterfaceBlockEntity.getFluidDataAccess(), barrelInterfaceBlockEntity.getSyncSlotsAccess()));

    private final TriFunction<Integer, Inventory, BarrelInterfaceBlockEntity, AbstractContainerMenu> barrelInterfaceFunction;

    DisplayTypes(TriFunction<Integer,Inventory,BarrelInterfaceBlockEntity,AbstractContainerMenu> barrelInterfaceFunction) {
        this.barrelInterfaceFunction = barrelInterfaceFunction;
    }

    @Override
    public AbstractContainerMenu createBarrelInterfaceMenu(int pContainerId, Inventory pPlayerInventory, BarrelInterfaceBlockEntity barrelInterfaceBlockEntity) {
        return barrelInterfaceFunction.apply(pContainerId,pPlayerInventory,barrelInterfaceBlockEntity);
    }
}
