package tfar.itemrepository.net;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CRefreshClientStacksPacket {

  private final int size;
  private final List<ItemStack> stacks;
  private final List<Integer> ints;
  public S2CRefreshClientStacksPacket(List<ItemStack> stacks,List<Integer> ints) {
    super();
    this.stacks = stacks;
    size = stacks.size();
    this.ints = ints;
  }

  public static void handle(S2CRefreshClientStacksPacket message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof RepositoryScreen repositoryScreen) {
        repositoryScreen.setGuiStacks(message.stacks,message.ints);
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
    for (int i = 0; i < msg.ints.size();i++) {
      buf.writeInt(msg.ints.get(i));
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

    List<Integer> ints = new ArrayList<>();
    for (int i = 0; i < size;i++) {
      ints.add(buf.readInt());
    }

    return new S2CRefreshClientStacksPacket(stacks,ints);
  }
}
