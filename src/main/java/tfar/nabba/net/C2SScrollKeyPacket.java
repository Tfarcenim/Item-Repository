package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.nabba.item.KeyItem;
import tfar.nabba.item.KeyRingItem;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SScrollKeyPacket implements C2SPacketHelper {

    private final boolean right;

    public C2SScrollKeyPacket(boolean up) {
        this.right = up;
    }

    public C2SScrollKeyPacket(FriendlyByteBuf buf) {
        right = buf.readBoolean();
    }

    public static void send(boolean up) {
        PacketHandler.sendToServer(new C2SScrollKeyPacket(up));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(right);
    }

    public void handleServer(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof KeyRingItem) {
            KeyRingItem.scrollKey(player.getMainHandItem(),right);
        }
    }
}

