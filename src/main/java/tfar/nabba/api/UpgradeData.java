package tfar.nabba.api;

import net.minecraft.world.item.Item;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

import java.util.function.Supplier;

public interface UpgradeData {

    int getSlotRequirement();

    //todo, config stuff
    void setSlotsRequired(int slotsRequired);

    int getAdditionalStorageStacks();
    void onUpgrade(BetterBarrelBlockEntity betterBarrelBlockEntity);
    Supplier<Item> getItem();

}
