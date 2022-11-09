package tfar.nabba.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import tfar.nabba.world.RepositoryInventory;

public class VanityKeyMenu extends AntiBarrelMenu{
    public VanityKeyMenu(int pContainerId, Inventory inventory, ContainerLevelAccess pAccess, RepositoryInventory repositoryInventory, ContainerData data, ContainerData syncSlots) {
        super(pContainerId, inventory, pAccess, repositoryInventory, data, syncSlots);
    }
}
