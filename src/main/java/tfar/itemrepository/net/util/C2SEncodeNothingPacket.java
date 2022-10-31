package tfar.itemrepository.net.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class C2SEncodeNothingPacket implements C2SPacketHelper {

    protected static C2SEncodeNothingPacket decodeNothing(FriendlyByteBuf buf) {
        return new C2SEncodeNothingPacket();
    }
    @Override
    public void handleServer(ServerPlayer player) {

    }

    @Override
    public void encode(FriendlyByteBuf buf) {

    }
}
