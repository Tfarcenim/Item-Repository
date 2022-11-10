package tfar.nabba.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tfar.nabba.NABBA;
import tfar.nabba.api.UpgradeData;
import tfar.nabba.api.UpgradeDataStack;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModItems;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static tfar.nabba.util.Utils.*;

public enum UpgradeDatas implements UpgradeData {
    DUMMY(0,0,NOTHING,() -> Items.AIR),
    STORAGE(1,BASE_STORAGE,Utils.add_to_internal_upgrades,() -> ModItems.STORAGE_UPGRADE),
    VOID(1,0,Utils.apply_void,() -> ModItems.VOID_UPGRADE,1),
    PICKUP(8,0,Utils.add_to_internal_upgrades,() -> ModItems.PICKUP_3x3_UPGRADE,9, PICKUP_TICK),
    INFINITE_VENDING(1000000000,0,Utils.add_to_internal_upgrades,() -> ModItems.INFINITE_VENDING_UPGRADE,1);

    private int slotsRequired;
    private final int additionalStorage;
    private final BiConsumer<BetterBarrelBlockEntity, UpgradeDataStack> onUpgrade;
    private final BiConsumer<BetterBarrelBlockEntity,UpgradeDataStack> onTick;
    private final Supplier<Item> itemSupplier;
    private final int maxAllowed;

    UpgradeDatas(int slotsRequired, int additionalStorage,BiConsumer<BetterBarrelBlockEntity,UpgradeDataStack> onUpgrade, Supplier<Item> itemSupplier) {
        this(slotsRequired,additionalStorage,onUpgrade,itemSupplier,32768);
    }

    UpgradeDatas(int slotsRequired, int additionalStorage,BiConsumer<BetterBarrelBlockEntity,UpgradeDataStack> onUpgrade, Supplier<Item> itemSupplier,int maxAllowed) {
        this(slotsRequired,additionalStorage,onUpgrade,itemSupplier,maxAllowed,NOTHING);
    }

    UpgradeDatas(int slotsRequired, int additionalStorage,BiConsumer<BetterBarrelBlockEntity,UpgradeDataStack> onUpgrade,
                 Supplier<Item> itemSupplier,int maxAllowed,BiConsumer<BetterBarrelBlockEntity,UpgradeDataStack> onTick) {
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
    public void onUpgrade(BetterBarrelBlockEntity betterBarrelBlockEntity,UpgradeDataStack stack) {
        onUpgrade.accept(betterBarrelBlockEntity,stack);
    }

    @Override
    public Supplier<Item> getItem() {
        return itemSupplier;
    }

    @Override
    public void tick(BetterBarrelBlockEntity barrelBlockEntity,UpgradeDataStack upgradeDataStack) {
        onTick.accept(barrelBlockEntity,upgradeDataStack);
    }

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(NABBA.MODID,name().toLowerCase(Locale.ROOT));
    }
}
