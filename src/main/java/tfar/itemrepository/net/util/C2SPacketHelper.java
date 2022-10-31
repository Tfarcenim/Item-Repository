package tfar.itemrepository.net.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface C2SPacketHelper {
    void handleInternal(ServerPlayer player);

    default void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(()-> handleInternal(player));
        ctx.get().setPacketHandled(true);
    }
}
