package tfar.nabba.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tfar.nabba.NABBA;
import tfar.nabba.api.Upgrade;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModItems;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static tfar.nabba.util.Utils.*;

public enum Upgrades implements Upgrade {
    DUMMY(0,0,NOTHING,() -> Items.AIR),
    STORAGE(1,BASE_STORAGE,Utils.add_to_internal_upgrades,() -> ModItems.STORAGE_UPGRADE),
    VOID(1,0,Utils.apply_void,() -> ModItems.VOID_UPGRADE,1),
    PICKUP(8,0,Utils.add_to_internal_upgrades,() -> ModItems.PICKUP_1x1_UPGRADE,9, PICKUP_TICK),
    INFINITE_VENDING(2000000000,0,Utils.add_to_internal_upgrades,() -> ModItems.INFINITE_VENDING_UPGRADE,1);

    private int slotsRequired;
    private final int additionalStorage;
    private final BiConsumer<BetterBarrelBlockEntity, UpgradeStack> onUpgrade;
    private final BiConsumer<BetterBarrelBlockEntity, UpgradeStack> onTick;
    private final Supplier<Item> itemSupplier;
    private final int maxAllowed;

    Upgrades(int slotsRequired, int additionalStorage, BiConsumer<BetterBarrelBlockEntity, UpgradeStack> onUpgrade, Supplier<Item> itemSupplier) {
        this(slotsRequired,additionalStorage,onUpgrade,itemSupplier,64000);
    }

    Upgrades(int slotsRequired, int additionalStorage, BiConsumer<BetterBarrelBlockEntity, UpgradeStack> onUpgrade, Supplier<Item> itemSupplier, int maxAllowed) {
        this(slotsRequired,additionalStorage,onUpgrade,itemSupplier,maxAllowed,NOTHING);
    }

    Upgrades(int slotsRequired, int additionalStorage, BiConsumer<BetterBarrelBlockEntity, UpgradeStack> onUpgrade,
             Supplier<Item> itemSupplier, int maxAllowed, BiConsumer<BetterBarrelBlockEntity, UpgradeStack> onTick) {
        this.slotsRequired = slotsRequired;
        this.additionalStorage = additionalStorage;
        this.onUpgrade = onUpgrade;

        this.itemSupplier = itemSupplier;
        this.maxAllowed = maxAllowed;
        this.onTick = onTick;
        REGISTRY.put(this.getKey(),this);
    }

    public void setSlotsRequired(int slotsRequired) {
        this.slotsRequired = slotsRequired;
    }

    @Override
    public int getMaxStackSize() {
        return maxAllowed;
    }

    @Override
    public int getSlotRequirement() {
        return slotsRequired;
    }

    @Override
    public int getStorageBonus() {
        return additionalStorage;
    }

    @Override
    public void onUpgrade(BetterBarrelBlockEntity betterBarrelBlockEntity, UpgradeStack stack) {
        onUpgrade.accept(betterBarrelBlockEntity,stack);
    }

    @Override
    public Supplier<Item> getItem() {
        return itemSupplier;
    }

    @Override
    public void tick(BetterBarrelBlockEntity barrelBlockEntity, UpgradeStack upgradeStack) {
        onTick.accept(barrelBlockEntity, upgradeStack);
    }

    @Override
    public ResourceLocation getKey() {
        return new ResourceLocation(NABBA.MODID,name().toLowerCase(Locale.ROOT));
    }
}
