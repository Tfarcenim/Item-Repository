package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryMenu;

import java.util.function.Supplier;

public class C2SGetDisplayPacket {

    public C2SGetDisplayPacket() {
    }

    //decode
    public C2SGetDisplayPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(  ()->  {
            AbstractContainerMenu container = player.containerMenu;
            if (container instanceof RepositoryMenu repositoryMenu) {
                repositoryMenu.refreshDisplay(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
