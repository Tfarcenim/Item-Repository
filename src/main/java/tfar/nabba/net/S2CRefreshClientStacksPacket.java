package tfar.nabba.net;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.RepositoryScreen;
import tfar.nabba.net.util.S2CPacketHelper;

import java.util.ArrayList;
import java.util.List;

public class S2CRefreshClientStacksPacket implements S2CPacketHelper {

  private final int size;
  private final List<ItemStack> stacks;
  private final List<Integer> ints;
  public S2CRefreshClientStacksPacket(List<ItemStack> stacks,List<Integer> ints) {
    this.stacks = stacks;
    size = stacks.size();
    this.ints = ints;
  }

  public void handleClient() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof RepositoryScreen repositoryScreen) {
        repositoryScreen.setGuiStacks(stacks,ints);
      }
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(size);
    for (ItemStack stack : stacks) {
      buf.writeNbt(stack.serializeNBT());
      buf.writeInt(stack.getCount());
    }
    for (int i = 0; i < ints.size();i++) {
      buf.writeInt(ints.get(i));
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
