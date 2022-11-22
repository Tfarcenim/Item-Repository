package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SExtractFluidPacket implements C2SPacketHelper {

    private final FluidStack stack;
    private final boolean shift;

    public C2SExtractFluidPacket(FluidStack stack, boolean shift) {
        this.stack = stack;
        this.shift = shift;
    }

    //decode
    public C2SExtractFluidPacket(FriendlyByteBuf buf) {
        this.stack = buf.readFluidStack();
        this.shift = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFluidStack(stack);
        buf.writeBoolean(shift);
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableFluidMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleFluidExtract(player, stack,shift);
        }
    }
}
