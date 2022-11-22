package tfar.nabba.util;

import net.minecraft.world.level.block.Block;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.init.ModBlocks;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public enum BarrelFrameTiers implements BarrelFrameTier {
    WOOD(0,Map.of(BarrelType.BETTER,() -> ModBlocks.BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.FLUID_BARREL)),
    STONE(1,Map.of(BarrelType.BETTER,() -> ModBlocks.STONE_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.STONE_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.STONE_FLUID_BARREL)),
    COPPER(4,Map.of(BarrelType.BETTER,() -> ModBlocks.COPPER_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.COPPER_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.COPPER_FLUID_BARREL)),
    IRON(8,Map.of(BarrelType.BETTER,() -> ModBlocks.IRON_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.IRON_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.IRON_FLUID_BARREL)),
    LAPIS(16,Map.of(BarrelType.BETTER,() -> ModBlocks.LAPIS_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.LAPIS_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.LAPIS_FLUID_BARREL)),
    GOLD(64,Map.of(BarrelType.BETTER,() -> ModBlocks.GOLD_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.GOLD_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.GOLD_FLUID_BARREL)),
    DIAMOND(128,Map.of(BarrelType.BETTER,() -> ModBlocks.DIAMOND_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.DIAMOND_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.DIAMOND_FLUID_BARREL)),
    EMERALD(256,Map.of(BarrelType.BETTER,() -> ModBlocks.EMERALD_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.EMERALD_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.EMERALD_FLUID_BARREL)),
    NETHERITE(2048,Map.of(BarrelType.BETTER,() -> ModBlocks.NETHERITE_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.NETHERITE_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.NETHERITE_FLUID_BARREL)),
    CREATIVE(Integer.MAX_VALUE,Map.of(BarrelType.BETTER,() -> ModBlocks.CREATIVE_BETTER_BARREL,BarrelType.ANTI,() -> ModBlocks.CREATIVE_ANTI_BARREL,BarrelType.FLUID,() -> ModBlocks.CREATIVE_FLUID_BARREL));
    private int upgradeSlots;
    private final Map<BarrelType,Supplier<AbstractBarrelBlock>> barrels;

    BarrelFrameTiers(int upgradeSlots, Map<BarrelType,Supplier<AbstractBarrelBlock>> barrels) {
        this.upgradeSlots = upgradeSlots;
        this.barrels = barrels;
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
    public AbstractBarrelBlock getBarrel(BarrelType type) {
        return barrels.get(type).get();
    }

    @Override
    public int getUpgradeSlots() {
        return upgradeSlots;
    }

    @Override
    public void setUpgradeSlots(int upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
