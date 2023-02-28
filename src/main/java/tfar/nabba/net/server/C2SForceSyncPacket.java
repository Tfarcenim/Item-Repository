package tfar.nabba.net.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.net.util.C2SPacketHelper;

public class C2SForceSyncPacket implements C2SPacketHelper {

    public C2SForceSyncPacket() {
    }

    //decode
    public C2SForceSyncPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableMenu<?> antiBarrelMenu) {
            antiBarrelMenu.refreshDisplay(player,true);
        }
    }
}
