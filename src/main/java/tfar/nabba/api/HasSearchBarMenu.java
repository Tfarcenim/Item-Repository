package tfar.nabba.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.S2CRefreshClientStacksPacket;

import java.util.ArrayList;
import java.util.List;

public interface HasSearchBarMenu {

    default void handleSearch(ServerPlayer player, String search) {
        getAccess().execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HasSearchBar repositoryBlock) {
                repositoryBlock.setSearchString(search);
            }
        });
        refreshDisplay(player);
    }

    void refreshDisplay(ServerPlayer player);

    DataSlot getRowSlot();

    ContainerLevelAccess getAccess();

}
