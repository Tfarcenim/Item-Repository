package tfar.nabba.net;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.net.util.S2CPacketHelper;

import java.util.ArrayList;
import java.util.List;

public class S2CRefreshClientFluidStacksPacket implements S2CPacketHelper {

  private final int size;
  private final List<FluidStack> stacks;
  private final List<Integer> ints;
  public S2CRefreshClientFluidStacksPacket(List<FluidStack> stacks, List<Integer> ints) {
    this.stacks = stacks;
    size = stacks.size();
    this.ints = ints;
  }

  public void handleClient() {
      Minecraft mc = Minecraft.getInstance();
   if (mc.screen instanceof SearchableFluidScreen<?,?> searchableScreen) {
        searchableScreen.setGuiFluids(stacks,ints);
      }
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(size);
    for (FluidStack stack : stacks) {
      buf.writeFluidStack(stack);
    }
    for (int i = 0; i < ints.size();i++) {
      buf.writeInt(ints.get(i));
    }
  }

  public static S2CRefreshClientFluidStacksPacket decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<FluidStack> stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      FluidStack stack = buf.readFluidStack();
      stacks.add(stack);
    }

    List<Integer> ints = new ArrayList<>();
    for (int i = 0; i < size;i++) {
      ints.add(buf.readInt());
    }

    return new S2CRefreshClientFluidStacksPacket(stacks,ints);
  }
}
