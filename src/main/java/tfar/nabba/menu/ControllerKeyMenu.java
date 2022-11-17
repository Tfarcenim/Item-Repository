package tfar.nabba.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.init.ModMenuTypes;

public class ControllerKeyMenu extends SearchableMenu<ControllerBlockEntity.ControllerHandler> {



    public ControllerKeyMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, ControllerBlockEntity.ControllerHandler controllerHandler, ContainerData inventoryData, ContainerData syncSlots) {
        this(ModMenuTypes.CONTROLLER_KEY, pContainerId, inventory,pAccess, controllerHandler, inventoryData,syncSlots);
    }

    public ControllerKeyMenu(int i, Inventory inventory) {
        this(ModMenuTypes.CONTROLLER_KEY, i, inventory,ContainerLevelAccess.NULL, null,new SimpleContainerData(2),new SimpleContainerData(54));
    }

    protected ControllerKeyMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, ControllerBlockEntity.ControllerHandler controllerHandler, ContainerData
            inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId,access,controllerHandler,inventoryData,syncSlots);
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
        addDataSlots(inventoryData);
    }
}
