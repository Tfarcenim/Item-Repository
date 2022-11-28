package tfar.nabba.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.api.FluidMenuProvider;

import javax.annotation.Nullable;

public class FluidControllerKeyMenuProvider implements MenuProvider {

    private final FluidMenuProvider controllerBlockEntity;

    public FluidControllerKeyMenuProvider(FluidMenuProvider controllerBlockEntity) {
        this.controllerBlockEntity = controllerBlockEntity;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Fluid Con. Key");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return controllerBlockEntity.createFluidMenu(i, playerInventory, player);
    }
}
