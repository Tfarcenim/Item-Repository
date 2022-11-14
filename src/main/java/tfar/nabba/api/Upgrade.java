package tfar.nabba.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.util.BarrelType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface Upgrade {

    Map<ResourceLocation, Upgrade> REGISTRY = new HashMap<>();

    int getSlotRequirement();

    //todo, config stuff
    void setSlotsRequired(int slotsRequired);
    int getMaxStackSize();

    int getStorageBonus(BarrelType type);
    void onUpgrade(AbstractBarrelBlockEntity betterBarrelBlockEntity, UpgradeStack stack);
    Supplier<Item> getItem();
    BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> NOTHING = (b, u) -> {};
    Map<BarrelType,Integer> NO_STORAGE = Map.of(BarrelType.BETTER,0,BarrelType.ANTI,0,BarrelType.FLUID,0);
    void tick(AbstractBarrelBlockEntity barrelBlockEntity, UpgradeStack upgradeStack);

    ResourceLocation getKey();

}
