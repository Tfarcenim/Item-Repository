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

        INSTANCE.registerMessage(id++, C2SScroll.class,
                C2SScroll::encode,
                C2SScroll::new,
                C2SScroll::handle);    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
