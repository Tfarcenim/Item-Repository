package tfar.nabba.inventory;

import net.minecraft.server.level.ServerPlayer;

public class FakeSlotSynchronizer {

    private final ServerPlayer player;

    public FakeSlotSynchronizer(ServerPlayer player) {

        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
