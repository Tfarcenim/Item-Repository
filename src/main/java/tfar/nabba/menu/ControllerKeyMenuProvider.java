package tfar.nabba.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.api.ItemMenuProvider;
import tfar.nabba.blockentity.ControllerBlockEntity;

import javax.annotation.Nullable;

public class ControllerKeyMenuProvider implements MenuProvider {

    private final ItemMenuProvider controllerBlockEntity;

    public ControllerKeyMenuProvider(ItemMenuProvider controllerBlockEntity) {
        this.controllerBlockEntity = controllerBlockEntity;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Controller Key");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return controllerBlockEntity.createItemMenu(i, playerInventory, player);
    }
}
