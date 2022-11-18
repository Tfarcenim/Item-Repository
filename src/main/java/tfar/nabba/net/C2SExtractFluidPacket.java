package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SExtractFluidPacket implements C2SPacketHelper {

    private final int slot;
    private final boolean shift;

    public C2SExtractFluidPacket(int slot, boolean shift) {
        this.slot = slot;
        this.shift = shift;
    }

    //decode
    public C2SExtractFluidPacket(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.shift = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(shift);
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableFluidMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleFluidExtract(player, slot,shift);
        }
    }
}
