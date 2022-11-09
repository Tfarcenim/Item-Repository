package tfar.nabba.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

import javax.annotation.Nullable;

public class VanityKeyMenuProvider implements MenuProvider {

    private final BetterBarrelBlockEntity betterBarrelBlockEntity;

    public VanityKeyMenuProvider(BetterBarrelBlockEntity betterBarrelBlockEntity) {
        this.betterBarrelBlockEntity = betterBarrelBlockEntity;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Vanity Key");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new VanityKeyMenu(i,playerInventory, ContainerLevelAccess.create(betterBarrelBlockEntity.getLevel(),betterBarrelBlockEntity.getBlockPos()));
    }
}
