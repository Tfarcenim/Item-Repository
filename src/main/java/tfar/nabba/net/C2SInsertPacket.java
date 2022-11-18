package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SInsertPacket implements C2SPacketHelper {

    int slot;
    public C2SInsertPacket(int slot) {
        this.slot = slot;
    }

    //decode
    public C2SInsertPacket(FriendlyByteBuf buf) {
        slot =  buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableItemMenu<?> searchableMenu) {
            searchableMenu.handleInsert(player,slot);
        }
    }
}
