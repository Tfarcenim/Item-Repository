package tfar.nabba.net;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.net.util.ItemStackUtil;
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
   if (mc.screen instanceof SearchableItemScreen<?,?> searchableScreen) {
        searchableScreen.setGuiStacks(stacks,ints);
      }
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(size);
    for (ItemStack stack : stacks) {
      //these stacks can be large
      ItemStackUtil.writeExtendedItemStack(buf,stack);
    }
    for (int i = 0; i < ints.size();i++) {
      buf.writeInt(ints.get(i));
    }
  }

  public static S2CRefreshClientStacksPacket decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<ItemStack> stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      //ditto
      ItemStack stack = ItemStackUtil.readExtendedItemStack(buf);
      stacks.add(stack);
    }

    List<Integer> ints = new ArrayList<>();
    for (int i = 0; i < size;i++) {
      ints.add(buf.readInt());
    }

    return new S2CRefreshClientStacksPacket(stacks,ints);
  }
}
