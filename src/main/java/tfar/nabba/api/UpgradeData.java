package tfar.nabba.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface UpgradeData {

    Map<ResourceLocation,UpgradeData> MAP = new HashMap<>();

    int getSlotRequirement();

    //todo, config stuff
    void setSlotsRequired(int slotsRequired);
    int getMaxStackSize();

    int getStorageBonus();
    void onUpgrade(BetterBarrelBlockEntity betterBarrelBlockEntity,UpgradeDataStack stack);
    Supplier<Item> getItem();
    BiConsumer<BetterBarrelBlockEntity,UpgradeDataStack> NOTHING = (b,u) -> {};
    void tick(BetterBarrelBlockEntity barrelBlockEntity,UpgradeDataStack upgradeDataStack);

    ResourceLocation getName();

}
