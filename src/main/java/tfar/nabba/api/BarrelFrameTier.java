package tfar.nabba.api;

import net.minecraft.world.level.block.Block;
import tfar.nabba.util.BarrelType;

import java.util.ArrayList;
import java.util.List;

public interface BarrelFrameTier {
    Block getBarrel(BarrelType type);
    int getUpgradeSlots();
    void setUpgradeSlots(int slots);
    List<BarrelFrameTier> tiers = new ArrayList<>();
    default int getTier() {
        return tiers.indexOf(this);
    }
    String getName();
}
