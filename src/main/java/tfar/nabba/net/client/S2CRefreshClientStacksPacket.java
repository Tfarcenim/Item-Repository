package tfar.nabba.net.client;

import tfar.nabba.net.util.S2CPacketHelper;

import java.util.List;

public abstract class S2CRefreshClientStacksPacket<S> implements S2CPacketHelper {

    protected final int size;
    protected final List<S> stacks;

    public S2CRefreshClientStacksPacket(List<S> stacks) {
        this.stacks = stacks;
        size = stacks.size();
    }

}
