package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.itemrepository.RepositoryMenu;
import tfar.itemrepository.net.util.C2SPacketHelper;

public class C2SInsertPacket implements C2SPacketHelper {
    public C2SInsertPacket() {
    }

    //decode
    public C2SInsertPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handleInternal(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof RepositoryMenu repositoryMenu) {
            repositoryMenu.handleInsert(player);
        }
    }
}
