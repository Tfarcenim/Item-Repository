package tfar.nabba.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.init.ModMenuTypes;

public class ControllerKeyFluidMenu extends SearchableFluidMenu<ControllerBlockEntity.ControllerFluidHandler> {



    public ControllerKeyFluidMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, ControllerBlockEntity.ControllerFluidHandler controllerHandler, ContainerData inventoryData, ContainerData syncSlots) {
        this(ModMenuTypes.FLUID_CONTROLLER_KEY, pContainerId, inventory,pAccess, controllerHandler, inventoryData,syncSlots);
    }

    public ControllerKeyFluidMenu(int i, Inventory inventory) {
        this(ModMenuTypes.FLUID_CONTROLLER_KEY, i, inventory,ContainerLevelAccess.NULL, null,new SimpleContainerData(2),new SimpleContainerData(54));
    }

    protected ControllerKeyFluidMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, ControllerBlockEntity.ControllerFluidHandler controllerHandler, ContainerData
            inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId, inventory, access,controllerHandler,inventoryData,syncSlots);
    }
}
