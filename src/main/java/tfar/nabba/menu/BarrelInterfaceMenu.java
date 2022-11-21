package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;
import tfar.nabba.init.ModMenuTypes;

public class BarrelInterfaceMenu extends SearchableItemMenu<BarrelInterfaceBlockEntity.BarrelInterfaceItemHandler> {

    public BarrelInterfaceMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access,
                               BarrelInterfaceBlockEntity.BarrelInterfaceItemHandler handler, ContainerData inventoryData,ContainerData syncSlots) {
        super(pMenuType, pContainerId,inventory,access,handler,inventoryData,syncSlots);
        addPlayerInv(inventory);
    }

    public BarrelInterfaceMenu(int containerId, Inventory inventory) {
        this(containerId, inventory,ContainerLevelAccess.NULL,null,new SimpleContainerData(2),new SimpleContainerData(54));
    }

    public BarrelInterfaceMenu(int containerId, Inventory inventory,ContainerLevelAccess access, BarrelInterfaceBlockEntity.BarrelInterfaceItemHandler handler,
                               ContainerData inventoryData,ContainerData syncSlots) {
        this(ModMenuTypes.BARREL_INTERFACE,containerId,inventory,access,handler,inventoryData,syncSlots);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (itemHandler.clientNeedsUpdate) {
            refreshDisplay((ServerPlayer) inventory.player);
            itemHandler.clientNeedsUpdate = false;
        }
     }
}
