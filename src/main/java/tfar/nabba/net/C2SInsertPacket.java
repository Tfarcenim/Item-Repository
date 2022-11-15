package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.menu.ControllerKeyMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SInsertPacket implements C2SPacketHelper {
    public C2SInsertPacket() {
    }

    //decode
    public C2SInsertPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AntiBarrelMenu antiBarrelMenu) {
            antiBarrelMenu.handleInsert(player);
        } else if (container instanceof ControllerKeyMenu controllerKeyMenu) {
            controllerKeyMenu.handleInsert(player);
        }
    }
}
