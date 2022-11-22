package tfar.nabba.net.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.net.util.S2CPacketHelper;

import java.util.List;

public class S2CRefreshClientFluidStacksPacket extends S2CRefreshClientStacksPacket<FluidStack> {

  public S2CRefreshClientFluidStacksPacket(List<FluidStack> stacks) {
    super(stacks);
  }

  public void handleClient() {
      Minecraft mc = Minecraft.getInstance();
   if (mc.screen instanceof SearchableFluidScreen<?,?> searchableScreen) {
        searchableScreen.setGuiFluids(stacks);
      }
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(size);
    for (FluidStack stack : stacks) {
      buf.writeFluidStack(stack);
    }
  }

  public static S2CRefreshClientFluidStacksPacket decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<FluidStack> stacks = Lists.newArrayList();
    for (int i = 0; i < size; i++) {
      FluidStack stack = buf.readFluidStack();
      stacks.add(stack);
    }
    return new S2CRefreshClientFluidStacksPacket(stacks);
  }
}
