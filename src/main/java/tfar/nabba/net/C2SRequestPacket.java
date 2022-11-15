package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.menu.ControllerKeyMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SRequestPacket implements C2SPacketHelper {

    private final int slot;
    private final int amount;
    private final boolean shift;

    public C2SRequestPacket(int slot, int amount, boolean shift) {
        this.slot = slot;
        this.amount = amount;
        this.shift = shift;
    }

    //decode
    public C2SRequestPacket(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.amount = buf.readInt();
        this.shift = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeInt(amount);
        buf.writeBoolean(shift);
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AntiBarrelMenu antiBarrelMenu) {
            antiBarrelMenu.handleRequest(player, slot, amount, shift);
        } else if (container instanceof ControllerKeyMenu controllerKeyMenu) {
            controllerKeyMenu.handleRequest(player,slot,amount,shift);
        }
    }
}
