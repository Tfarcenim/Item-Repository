package tfar.nabba.net.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.menu.VanityKeyMenu;
import tfar.nabba.net.util.C2SPacketHelper;


public class C2SVanityPacket implements C2SPacketHelper {

  int color;
  double size;
  public C2SVanityPacket(int color, double size) {
    this.color = color;
    this.size = size;
  }

  //decode
  public C2SVanityPacket(FriendlyByteBuf buf) {
    this.color = buf.readInt();
    this.size = buf.readDouble();
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(color);
    buf.writeDouble(size);
  }

  public void handleServer(ServerPlayer player) {
    AbstractContainerMenu container = player.containerMenu;
    if (container instanceof VanityKeyMenu vanityKeyMenu) {
      vanityKeyMenu.receiveVanity(color,size);
    }
  }
}

