package tfar.nabba.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class VanityKeyMenuProvider implements MenuProvider {

    private final BlockPos pos;

    public VanityKeyMenuProvider(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Vanity Key");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new VanityKeyMenu(i,playerInventory, pos);
    }
}
