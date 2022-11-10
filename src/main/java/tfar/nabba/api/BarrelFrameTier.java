package tfar.nabba.api;

import java.util.ArrayList;
import java.util.List;

public interface BarrelFrameTier {

    int getUpgradeSlots();
    void setUpgradeSlots(int slots);
    List<BarrelFrameTier> tiers = new ArrayList<>();
    default int getTier() {
        return tiers.indexOf(this);
    }
}
