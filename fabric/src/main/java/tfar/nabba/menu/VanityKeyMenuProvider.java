package tfar.nabba.menu;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class VanityKeyMenuProvider implements ExtendedScreenHandlerFactory {

    private final BlockPos pos;

    public VanityKeyMenuProvider(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("nabba.menu.vanity_key");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new VanityKeyMenu(i,playerInventory, pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
