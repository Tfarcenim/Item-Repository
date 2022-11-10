package tfar.nabba.util;

import net.minecraft.world.item.Item;
import tfar.nabba.api.UpgradeData;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModItems;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static tfar.nabba.util.Utils.*;

public enum UpgradeDatas implements UpgradeData {
    x1_STORAGE (1,BASE_STORAGE,Utils.add_to_internal_upgrades,() -> ModItems.STORAGE_UPGRADE),
    x4_STORAGE(4,BASE_STORAGE * 4,Utils.add_to_internal_upgrades,() -> ModItems.x4_STORAGE_UPGRADE),
    x16_STORAGE(16,BASE_STORAGE * 16,Utils.add_to_internal_upgrades,() -> ModItems.x16_STORAGE_UPGRADE),
    x64_STORAGE(64,BASE_STORAGE * 64,Utils.add_to_internal_upgrades,() -> ModItems.x64_STORAGE_UPGRADE),
    x256_STORAGE(256,BASE_STORAGE * 256,Utils.add_to_internal_upgrades,() -> ModItems.x256_STORAGE_UPGRADE),
    x1024_STORAGE(1024,BASE_STORAGE * 1024,Utils.add_to_internal_upgrades,() -> ModItems.x1024_STORAGE_UPGRADE),
    VOID(1,0,Utils.apply_void,() -> ModItems.VOID_UPGRADE,1),
    PICKUP_3x3(27,0,Utils.add_to_internal_upgrades,() -> ModItems.PICKUP_3x3_UPGRADE,1,PICKUP_3x3_TICK),
    PICKUP_9x9(243,0,Utils.add_to_internal_upgrades,() -> ModItems.PICKUP_9x9_UPGRADE,1,PICKUP_9x9_TICK),
    INFINITE_STORAGE(1000000000,32000000,Utils.add_to_internal_upgrades,() -> ModItems.INFINITE_STORAGE_UPGRADE,1),
    INFINITE_VENDING(1000000000,0,Utils.add_to_internal_upgrades,() -> ModItems.INFINITE_VENDING_UPGRADE,1);

    private int slotsRequired;
    private int additionalStorage;
    private BiConsumer<BetterBarrelBlockEntity, UpgradeData> onUpgrade;
    private Consumer<BetterBarrelBlockEntity> onTick;
    private final Supplier<Item> itemSupplier;
    private int maxAllowed;

    UpgradeDatas(int slotsRequired, int additionalStorage,BiConsumer<BetterBarrelBlockEntity,UpgradeData> onUpgrade, Supplier<Item> itemSupplier) {
        this(slotsRequired,additionalStorage,onUpgrade,itemSupplier,32768);
    }

    UpgradeDatas(int slotsRequired, int additionalStorage,BiConsumer<BetterBarrelBlockEntity,UpgradeData> onUpgrade, Supplier<Item> itemSupplier,int maxAllowed) {
        this(slotsRequired,additionalStorage,onUpgrade,itemSupplier,maxAllowed,NOTHING);
    }

    UpgradeDatas(int slotsRequired, int additionalStorage,BiConsumer<BetterBarrelBlockEntity,UpgradeData> onUpgrade, Supplier<Item> itemSupplier,int maxAllowed,Consumer<BetterBarrelBlockEntity> onTick) {
        this.slotsRequired = slotsRequired;
        this.additionalStorage = additionalStorage;
        this.onUpgrade = onUpgrade;

        this.itemSupplier = itemSupplier;
        this.maxAllowed = maxAllowed;
        this.onTick = onTick;
    }

    public void setSlotsRequired(int slotsRequired) {
        this.slotsRequired = slotsRequired;
    }

    @Override
    public int maxAllowed() {
        return maxAllowed;
    }

    @Override
    public int getSlotRequirement() {
        return slotsRequired;
    }

    @Override
    public int getAdditionalStorageStacks() {
        return additionalStorage;
    }

    @Override
    public void onUpgrade(BetterBarrelBlockEntity betterBarrelBlockEntity) {
        onUpgrade.accept(betterBarrelBlockEntity,this);
    }

    @Override
    public Supplier<Item> getItem() {
        return itemSupplier;
    }

    @Override
    public void tick(BetterBarrelBlockEntity barrelBlockEntity) {
        onTick.accept(barrelBlockEntity);
    }
}
