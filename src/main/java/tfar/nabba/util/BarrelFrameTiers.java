package tfar.nabba.util;

import net.minecraft.world.level.block.Block;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.init.ModBlocks;

import java.util.function.Supplier;

public enum BarrelFrameTiers implements BarrelFrameTier {
    WOOD(0,() -> ModBlocks.BETTER_BARREL),
    STONE(1,() -> ModBlocks.STONE_BETTER_BARREL),
    COPPER(4,() -> ModBlocks.COPPER_BETTER_BARREL),
    IRON(8,() -> ModBlocks.IRON_BETTER_BARREL),
    LAPIS(16,() -> ModBlocks.LAPIS_BETTER_BARREL),
    GOLD(64,() -> ModBlocks.GOLD_BETTER_BARREL),
    DIAMOND(128,() -> ModBlocks.DIAMOND_BETTER_BARREL),
    EMERALD(256,() -> ModBlocks.EMERALD_BETTER_BARREL),
    NETHERITE(2048,() -> ModBlocks.NETHERITE_BETTER_BARREL),
    CREATIVE(Integer.MAX_VALUE,() -> ModBlocks.CREATIVE_BETTER_BARREL);
    private int upgradeSlots;
    private Supplier<Block> barrel;

    BarrelFrameTiers(int upgradeSlots, Supplier<Block> barrel) {
        this.upgradeSlots = upgradeSlots;
        this.barrel = barrel;
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
    public Block getBarrel() {
        return barrel.get();
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
