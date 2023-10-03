package tfar.nabba.net.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.ItemStackUtil;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClientPacketHandler {
    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(PacketHandler.refresh_items,ClientPacketHandler::receiveSyncItems);
        ClientPlayNetworking.registerGlobalReceiver(PacketHandler.refresh_fluids, ClientPacketHandler::receiveSyncFluids);
    }

    private static void receiveSyncItems(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        List<ItemStack> stacks = item_deserializer.apply(buf);
        client.execute(() -> {
            if (client.screen instanceof SearchableItemScreen<?, ?> searchableScreen) {
                searchableScreen.setGuiStacks(stacks);
            }
        });
    }

    private static void receiveSyncFluids(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        List<FabricFluidStack> stacks = fluid_deserializer.apply(buf);
        client.execute(() -> {
            if (client.screen instanceof SearchableFluidScreen<?, ?> searchableScreen) {
                searchableScreen.setGuiFluids(stacks);
            }
        });
    }

    public static Function<FriendlyByteBuf, List<ItemStack>> item_deserializer = buf -> {
      int size = buf.readInt();
      return IntStream.range(0,size).mapToObj(integer -> ItemStackUtil.readExtendedItemStack(buf)).collect(Collectors.toList());
    };

    public static Function<FriendlyByteBuf, List<FabricFluidStack>> fluid_deserializer = buf -> {
        int size = buf.readInt();
        return IntStream.range(0,size).mapToObj(integer -> FabricFluidStack.fromPacket(buf)).collect(Collectors.toList());
    };

}
