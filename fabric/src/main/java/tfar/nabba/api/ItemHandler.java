package tfar.nabba.api;

import tfar.nabba.shim.IItemHandlerShim;

public interface ItemHandler extends IItemHandlerShim {

    boolean isFull();

    default int getActualLimit() {
        return Integer.MAX_VALUE;
    }
}
