package tfar.nabba.net.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.util.ItemStackUtil;

import java.util.List;

public class S2CRefreshClientItemStacksPacket extends S2CRefreshClientStacksPacket<ItemStack> {

  public S2CRefreshClientItemStacksPacket(List<ItemStack> stacks) {
    super(stacks);
  }

  public void handleClient() {
      Minecraft mc = Minecraft.getInstance();
   if (mc.screen instanceof SearchableItemScreen<?,?> searchableScreen) {
        searchableScreen.setGuiStacks(stacks);
      }
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(size);
    for (ItemStack stack : stacks) {
      //these stacks can be large
      ItemStackUtil.writeExtendedItemStack(buf,stack);
    }
  }

  public static S2CRefreshClientItemStacksPacket decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<ItemStack> stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      //ditto
      ItemStack stack = ItemStackUtil.readExtendedItemStack(buf);
      stacks.add(stack);
    }
    return new S2CRefreshClientItemStacksPacket(stacks);
  }
}
