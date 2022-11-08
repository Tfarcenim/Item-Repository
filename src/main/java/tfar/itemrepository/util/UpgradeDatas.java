package tfar.itemrepository.util;

import static tfar.itemrepository.util.Utils.BASE_STORAGE;

public enum UpgradeDatas implements UpgradeData {
    x1_STORAGE (1,BASE_STORAGE),x4_STORAGE(4,BASE_STORAGE * 4),
    x16_STORAGE(16,BASE_STORAGE * 16), x64_STORAGE(64,BASE_STORAGE * 64),
    x256_STORAGE(256,BASE_STORAGE * 256), x1024_STORAGE(1024,BASE_STORAGE * 1024),

    VOID(1,0),
    PICKUP_3x3(27,0),PICKUP_9x9(729,0),
    INFINITE_STORAGE(1000000000,32000000),
    INFINITE_VENDING(1000000000,0);

    private int slotsRequired;
    private int additionalStorage;

    UpgradeDatas(int slotsRequired, int additionalStorage) {
        this.slotsRequired = slotsRequired;
        this.additionalStorage = additionalStorage;
    }

    //todo, config stuff
    public void setSlotsRequired(int slotsRequired) {
        this.slotsRequired = slotsRequired;
    }

    @Override
    public int getSlotRequirement() {
        return slotsRequired;
    }

    @Override
    public int getAdditionalStorageStacks() {
        return additionalStorage;
    }
}
