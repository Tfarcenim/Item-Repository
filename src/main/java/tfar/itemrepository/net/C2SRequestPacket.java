package tfar.itemrepository.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.itemrepository.RepositoryMenu;
import tfar.itemrepository.net.util.C2SPacketHelper;

import java.util.function.Supplier;

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

    public void handleInternal(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof RepositoryMenu repositoryMenu) {
            repositoryMenu.handleRequest(player, slot, amount, shift);
        }
    }
}
