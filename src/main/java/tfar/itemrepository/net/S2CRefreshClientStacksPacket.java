package tfar.itemrepository.net;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryScreen;

import java.util.List;
import java.util.function.Supplier;

public class S2CRefreshClientStacksPacket {

  private final int size;
  private final List<ItemStack> stacks;
  public S2CRefreshClientStacksPacket(List<ItemStack> stacks) {
    super();
    this.stacks = stacks;
    size = stacks.size();
  }

  public static void handle(S2CRefreshClientStacksPacket message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof RepositoryScreen repositoryScreen) {
        repositoryScreen.setGuiStacks(message.stacks);
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static void encode(S2CRefreshClientStacksPacket msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.size);
    for (ItemStack stack : msg.stacks) {
      buf.writeNbt(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
  }

  public static S2CRefreshClientStacksPacket decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<ItemStack> stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      CompoundTag stacktag = buf.readNbt();
      ItemStack stack = ItemStack.of(stacktag);
      stack.setCount(buf.readInt());
      stacks.add(stack);
    }
    return new S2CRefreshClientStacksPacket(stacks);
  }
}
