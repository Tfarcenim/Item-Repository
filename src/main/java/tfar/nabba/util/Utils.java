package tfar.nabba.util;

import tfar.nabba.blockentity.BetterBarrelBlockEntity;

import java.util.function.BiConsumer;

public class Utils {
    public static final int INVALID = -1;
    public static String ID = "id";
    public static final int BASE_STORAGE = 64;

    public static BiConsumer<BetterBarrelBlockEntity,UpgradeData> add_to_internal_upgrades = (betterBarrelBlockEntity, upgradeData) -> {
        int existing = betterBarrelBlockEntity.getUpgrades().getOrDefault(upgradeData,0);
        if (existing == 0) {
            betterBarrelBlockEntity.getUpgrades().put(upgradeData,1);
        } else {
            betterBarrelBlockEntity.getUpgrades().put(upgradeData,existing + 1);
        }
    };
}
