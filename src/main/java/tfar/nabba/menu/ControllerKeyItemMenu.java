package tfar.nabba.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.init.ModMenuTypes;

public class ControllerKeyItemMenu extends SearchableItemMenu<ControllerBlockEntity.ControllerHandler> {



    public ControllerKeyItemMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, ControllerBlockEntity.ControllerHandler controllerHandler, ContainerData inventoryData, ContainerData syncSlots) {
        this(ModMenuTypes.ITEM_CONTROLLER_KEY, pContainerId, inventory,pAccess, controllerHandler, inventoryData,syncSlots);
    }

    public ControllerKeyItemMenu(int i, Inventory inventory) {
        this(ModMenuTypes.ITEM_CONTROLLER_KEY, i, inventory,ContainerLevelAccess.NULL, null,new SimpleContainerData(2),new SimpleContainerData(54));
    }

    protected ControllerKeyItemMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, ControllerBlockEntity.ControllerHandler controllerHandler, ContainerData
            inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId, inventory, access,controllerHandler,inventoryData,syncSlots);
    }
}
