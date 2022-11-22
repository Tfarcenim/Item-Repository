package tfar.nabba.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.nabba.NABBA;

public class PacketHandler {
    public static SimpleChannel INSTANCE;

    public static void registerMessages() {
        int id = 0;

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(NABBA.MODID, NABBA.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(id++, C2SScrollPacket.class,
                C2SScrollPacket::encode,
                C2SScrollPacket::new,
                C2SScrollPacket::handle);

        INSTANCE.registerMessage(id++, C2SScrollKeyPacket.class,
                C2SScrollKeyPacket::encode,
                C2SScrollKeyPacket::new,
                C2SScrollKeyPacket::handle);

        INSTANCE.registerMessage(id++, C2SSearchPacket.class,
                C2SSearchPacket::encode,
                C2SSearchPacket::new,
                C2SSearchPacket::handle);

        INSTANCE.registerMessage(id++, C2SVanityPacket.class,
                C2SVanityPacket::encode,
                C2SVanityPacket::new,
                C2SVanityPacket::handle);

        INSTANCE.registerMessage(id++, C2SExtractItemPacket.class,
                C2SExtractItemPacket::encode,
                C2SExtractItemPacket::new,
                C2SExtractItemPacket::handle);

        INSTANCE.registerMessage(id++, C2SExtractFluidPacket.class,
                C2SExtractFluidPacket::encode,
                C2SExtractFluidPacket::new,
                C2SExtractFluidPacket::handle);

        INSTANCE.registerMessage(id++, C2SForceSyncPacket.class,
                C2SForceSyncPacket::encode,
                C2SForceSyncPacket::new,
                C2SForceSyncPacket::handle);

        INSTANCE.registerMessage(id++, C2SInsertPacket.class,
                C2SInsertPacket::encode,
                C2SInsertPacket::new,
                C2SInsertPacket::handle);

        INSTANCE.registerMessage(id++, S2CRefreshClientStacksPacket.class,
                S2CRefreshClientStacksPacket::encode,
                S2CRefreshClientStacksPacket::decode,
                S2CRefreshClientStacksPacket::handle);

        INSTANCE.registerMessage(id++, S2CRefreshClientFluidStacksPacket.class,
                S2CRefreshClientFluidStacksPacket::encode,
                S2CRefreshClientFluidStacksPacket::decode,
                S2CRefreshClientFluidStacksPacket::handle);
    }

    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }
}
