package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryMenu;

import java.util.function.Supplier;


public class C2SSearchPacket {

  String search;

  public C2SSearchPacket(){}

  public C2SSearchPacket(String search){
    this.search = search;
  }

  //decode
  public C2SSearchPacket(FriendlyByteBuf buf) {
    this.search = buf.readUtf();
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUtf(search);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;
      ctx.get().enqueueWork(  ()->  {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof RepositoryMenu repositoryMenu) {
          repositoryMenu.handleSearch(player, search);
        }
      });
      ctx.get().setPacketHandled(true);
    }
}

