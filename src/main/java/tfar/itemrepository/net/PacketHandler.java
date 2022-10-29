package tfar.itemrepository.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.itemrepository.ItemRepository;

public class PacketHandler {
    public static SimpleChannel INSTANCE;

    public static void registerMessages() {
        int id = 0;

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ItemRepository.MODID, ItemRepository.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(id++, C2SScrollPacket.class,
                C2SScrollPacket::encode,
                C2SScrollPacket::new,
                C2SScrollPacket::handle);

        INSTANCE.registerMessage(id++, C2SRequestPacket.class,
                C2SRequestPacket::encode,
                C2SRequestPacket::new,
                C2SRequestPacket::handle);

        INSTANCE.registerMessage(id++, C2SInsertPacket.class,
                C2SInsertPacket::encode,
                C2SInsertPacket::new,
                C2SInsertPacket::handle);


        INSTANCE.registerMessage(id++, C2SGetDisplayPacket.class,
                C2SGetDisplayPacket::encode,
                C2SGetDisplayPacket::new,
                C2SGetDisplayPacket::handle);

        INSTANCE.registerMessage(id++, S2CRefreshClientStacksPacket.class,
                S2CRefreshClientStacksPacket::encode,
                S2CRefreshClientStacksPacket::decode,
                S2CRefreshClientStacksPacket::handle);
    }

    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }
}
