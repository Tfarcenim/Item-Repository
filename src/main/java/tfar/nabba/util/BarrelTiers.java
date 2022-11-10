package tfar.nabba.util;

import tfar.nabba.api.BarrelFrameTier;

public enum BarrelTiers implements BarrelFrameTier {
    WOOD(0),
    STONE(1),
    COPPER(4),
    IRON(8),
    LAPIS(16),
    GOLD(64),
    DIAMOND(128),
    EMERALD(256),
    NETHERITE(2048),
    CREATIVE(Integer.MAX_VALUE);
    private int upgradeSlots;

    BarrelTiers(int upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    static {
        tiers.add(WOOD);
        tiers.add(STONE);
        tiers.add(COPPER);
        tiers.add(IRON);
        tiers.add(LAPIS);
        tiers.add(GOLD);
        tiers.add(DIAMOND);
        tiers.add(EMERALD);
        tiers.add(NETHERITE);
        tiers.add(CREATIVE);
    }
    @Override
    public int getUpgradeSlots() {
        return upgradeSlots;
    }

    @Override
    public void setUpgradeSlots(int upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }
}
