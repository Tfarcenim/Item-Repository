package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryMenu;
import tfar.itemrepository.net.util.C2SPacketHelper;

import java.util.function.Supplier;


public class C2SScrollPacket implements C2SPacketHelper {

  int scroll_amount;

  public C2SScrollPacket(){}

  public C2SScrollPacket(int amount){ this.scroll_amount = amount;}

  //decode
  public C2SScrollPacket(FriendlyByteBuf buf) {
    this.scroll_amount = buf.readInt();
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(scroll_amount);
  }
    public void handleInternal(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof RepositoryMenu repositoryMenu) {
          repositoryMenu.handleScroll(player,scroll_amount);
        }
    }
}

