package tfar.nabba.api;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public interface ItemMenuProvider {
    @Nullable AbstractContainerMenu createItemMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer);
}
