package tfar.nabba.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
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
}
