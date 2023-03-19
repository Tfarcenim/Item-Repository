package tfar.nabba.net.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SInsertPacket implements C2SPacketHelper {

    private final int count;

    public C2SInsertPacket(int count) {
        this.count = count;
    }

    //decode
    public C2SInsertPacket(FriendlyByteBuf buf) {
        count = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(count);
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableMenu<?> searchableMenu) {
            searchableMenu.handleInsert(player,count);
        }
    }
}
