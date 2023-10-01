package tfar.nabba.net;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.NABBA;
import tfar.nabba.item.keys.KeyRingItem;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.menu.VanityKeyMenu;
import tfar.nabba.util.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PacketHandler {

    public static final ResourceLocation scroll = new ResourceLocation(NABBA.MODID,"scroll");
    public static final ResourceLocation scroll_keyring = new ResourceLocation(NABBA.MODID,"scroll_keyring");
    public static final ResourceLocation search = new ResourceLocation(NABBA.MODID,"search");
    public static final ResourceLocation vanity = new ResourceLocation(NABBA.MODID,"vanity");
    public static final ResourceLocation extract_item = new ResourceLocation(NABBA.MODID,"extract_item");
    public static final ResourceLocation extract_fluid = new ResourceLocation(NABBA.MODID,"extract_fluid");
    public static final ResourceLocation force_sync = new ResourceLocation(NABBA.MODID,"force_sync");
    public static final ResourceLocation insert = new ResourceLocation(NABBA.MODID,"insert");
    public static final ResourceLocation refresh_items = new ResourceLocation(NABBA.MODID,"refresh_items");
    public static final ResourceLocation refresh_fluids = new ResourceLocation(NABBA.MODID,"refresh_fluids");

    public static final Map<ResourceLocation,Consumer<FriendlyByteBuf>> serializers = new HashMap<>();

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(scroll,PacketHandler::receiveScroll);
        ServerPlayNetworking.registerGlobalReceiver(scroll_keyring,PacketHandler::receiveScrollKeyring);
        ServerPlayNetworking.registerGlobalReceiver(search, PacketHandler::receiveSearch);
        ServerPlayNetworking.registerGlobalReceiver(vanity, PacketHandler::receiveVanity);
        ServerPlayNetworking.registerGlobalReceiver(extract_item, PacketHandler::receiveExtractItem);
        ServerPlayNetworking.registerGlobalReceiver(extract_fluid, PacketHandler::receiveExtractFluid);
        ServerPlayNetworking.registerGlobalReceiver(force_sync, PacketHandler::receiveForceRefresh);
        ServerPlayNetworking.registerGlobalReceiver(insert,PacketHandler::receiveInsert);
    }

    public static void sendToServer(ResourceLocation packet, Consumer<FriendlyByteBuf> consumer) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        consumer.accept(buf);
        ClientPlayNetworking.send(packet, buf);
    }

    public static void sendToClient(ServerPlayer player, ResourceLocation packet, Consumer<FriendlyByteBuf> consumer) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        consumer.accept(buf);
        ServerPlayNetworking.send(player,packet,buf);
    }

    private static void receiveScroll(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleScroll(player,buf.readInt());
        }
    }

    private static void receiveScrollKeyring(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (player.getMainHandItem().getItem() instanceof KeyRingItem) {
            CommonUtils.scrollKey(player.getMainHandItem(),buf.readBoolean());
        }
    }

    private static void receiveSearch(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableItemMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleSearch(player, buf.readUtf());//search
        }
    }

    private static void receiveVanity(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof VanityKeyMenu vanityKeyMenu) {
            vanityKeyMenu.receiveVanity(buf.readInt(),buf.readDouble());//color, size
        }
    }

    private static void receiveExtractItem(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableItemMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleItemExtract(player,buf.readItem(), buf.readBoolean());//stack, shift
        }
    }

    private static void receiveExtractFluid(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableFluidMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleFluidExtract(player,buf.readItem(), buf.readBoolean());//stack, shift
        }
    }

    private static void receiveForceRefresh(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableMenu<?> antiBarrelMenu) {
            antiBarrelMenu.refreshDisplay(player,true);
        }
    }

    private static void receiveInsert(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableMenu<?> searchableMenu) {
            searchableMenu.handleInsert(player,buf.readInt());//count
        }
    }
}
