package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.itemrepository.RepositoryMenu;
import tfar.itemrepository.net.util.C2SPacketHelper;


public class C2SSearchPacket implements C2SPacketHelper {

  String search;

  public C2SSearchPacket(){}

  public C2SSearchPacket(String search) {
    this.search = search;
  }

  //decode
  public C2SSearchPacket(FriendlyByteBuf buf) {
    this.search = buf.readUtf();
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUtf(search);
  }

  public void handleInternal(ServerPlayer player) {
    AbstractContainerMenu container = player.containerMenu;
    if (container instanceof RepositoryMenu repositoryMenu) {
      repositoryMenu.handleSearch(player, search);
    }
  }
}

