package tfar.nabba.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.net.util.C2SPacketHelper;
import tfar.nabba.net.util.ItemStackUtil;

public class C2SExtractItemPacket implements C2SPacketHelper {
    private final ItemStack stack;
    private final boolean shift;

    public C2SExtractItemPacket(ItemStack stack, boolean shift) {
        this.stack = stack;
        this.shift = shift;
    }

    //decode
    public C2SExtractItemPacket(FriendlyByteBuf buf) {
        this.stack = ItemStackUtil.readExtendedItemStack(buf);
        this.shift = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        ItemStackUtil.writeExtendedItemStack(buf,stack);
        buf.writeBoolean(shift);
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof SearchableItemMenu<?> antiBarrelMenu) {
            antiBarrelMenu.handleItemExtract(player,stack, shift);
        }
    }
}
