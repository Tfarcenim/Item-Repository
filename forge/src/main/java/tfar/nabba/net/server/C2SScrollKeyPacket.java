package tfar.nabba.net.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.nabba.item.keys.KeyRingItem;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.util.C2SPacketHelper;
import tfar.nabba.util.CommonUtils;

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
            CommonUtils.scrollKey(player.getMainHandItem(),right);
        }
    }
}

