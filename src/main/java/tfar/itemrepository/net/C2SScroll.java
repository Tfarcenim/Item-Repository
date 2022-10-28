package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryMenu;

import java.util.function.Supplier;


public class C2SScroll {

  int scroll_amount;

  public C2SScroll(){}

  public C2SScroll(int amount){ this.scroll_amount = amount;}

  //decode
  public C2SScroll(FriendlyByteBuf buf) {
    this.scroll_amount = buf.readInt();
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(scroll_amount);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      Player player = ctx.get().getSender();
      if (player == null) return;
      ctx.get().enqueueWork(  ()->  {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof RepositoryMenu repositoryMenu) {
          repositoryMenu.handleScroll(scroll_amount);

        }
      });
      ctx.get().setPacketHandled(true);
    }
}

