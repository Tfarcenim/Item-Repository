package tfar.nabba.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.api.DisplayMenuProvider;
import tfar.nabba.api.DisplayType;

import javax.annotation.Nullable;

public class ControllerKeyMenuProvider implements MenuProvider {

    private final DisplayMenuProvider controllerBlockEntity;
    private final DisplayType type;

    public ControllerKeyMenuProvider(DisplayMenuProvider controllerBlockEntity, DisplayType type) {
        this.controllerBlockEntity = controllerBlockEntity;
        this.type = type;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return controllerBlockEntity.createDisplayMenu(i, playerInventory, player, type);
    }
}
