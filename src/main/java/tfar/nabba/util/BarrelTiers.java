package tfar.nabba.util;

public enum BarrelTiers implements BarrelTier {
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

    @Override
    public int getUpgradeSlots() {
        return upgradeSlots;
    }
}
