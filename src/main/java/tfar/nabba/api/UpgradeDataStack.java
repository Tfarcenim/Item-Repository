package tfar.nabba.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.util.UpgradeDatas;

import java.util.Objects;

public class UpgradeDataStack {
    public static final UpgradeDataStack EMPTY = defaultInstance(UpgradeDatas.DUMMY).setImmutable(true);
    public static final UpgradeDataStack STORAGE = defaultInstance(UpgradeDatas.STORAGE).setImmutable(true);
    public static final UpgradeDataStack x4_STORAGE = new UpgradeDataStack(UpgradeDatas.STORAGE,4).setImmutable(true);
    public static final UpgradeDataStack x16_STORAGE = new UpgradeDataStack(UpgradeDatas.STORAGE,16).setImmutable(true);
    public static final UpgradeDataStack x64_STORAGE = new UpgradeDataStack(UpgradeDatas.STORAGE,64).setImmutable(true);
    public static final UpgradeDataStack x256_STORAGE = new UpgradeDataStack(UpgradeDatas.STORAGE,256).setImmutable(true);
    public static final UpgradeDataStack x1024_STORAGE = new UpgradeDataStack(UpgradeDatas.STORAGE,1024).setImmutable(true);
    public static final UpgradeDataStack INFINITE_STORAGE = new UpgradeDataStack(UpgradeDatas.STORAGE,32000).setImmutable(true);
    public static final UpgradeDataStack INFINITE_VENDING = defaultInstance(UpgradeDatas.INFINITE_VENDING).setImmutable(true);
    public static final UpgradeDataStack VOID = defaultInstance(UpgradeDatas.VOID).setImmutable(true);

    public static final UpgradeDataStack PICKUP_1x1 = defaultInstance(UpgradeDatas.PICKUP).setImmutable(true);
    public static final UpgradeDataStack PICKUP_3x3 = new UpgradeDataStack(UpgradeDatas.PICKUP,3).setImmutable(true);
    public static final UpgradeDataStack PICKUP_9x9 = new UpgradeDataStack(UpgradeDatas.PICKUP,9).setImmutable(true);

    private final UpgradeData data;
    private int count;
    private boolean immutable;

    public static UpgradeDataStack defaultInstance(UpgradeData upgradeData) {
        return new UpgradeDataStack(upgradeData);
    }

    public static UpgradeDataStack of(CompoundTag tag) {
        ResourceLocation name = new ResourceLocation(tag.getString("Upgrade"));
        return new UpgradeDataStack(UpgradeData.MAP.get(name),tag.getInt("Count"));
    }

    public UpgradeDataStack(UpgradeData data) {
        this(data,1);
    }
    public UpgradeDataStack(UpgradeData data, int count) {
        this.data = data;
        this.count = count;
    }

    public UpgradeDataStack setImmutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    public UpgradeData getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (immutable) {
            throw new UnsupportedOperationException();
        }
        this.count = count;
    }

    public void grow() {
        grow(1);
    }

    public void grow(int count) {
        setCount(getCount() + count);
    }

    public void shrink() {
        shrink(1);
    }

    public void shrink(int count) {
        setCount(count > getCount() ? 0: getCount() - count);
    }

    public int getUpgradeSlotsRequired() {
        return getData().getSlotRequirement() * getCount();
    }

    public int getMaxPermitted() {
        return getData().getMaxStackSize() / getCount();
    }

    public int getStorageStacks() {
        return getCount() * getData().getStorageBonus();
    }

    public UpgradeDataStack copy() {
        return new UpgradeDataStack(getData(),getCount());
    }

    public ItemStack createItemStack() {
        return new ItemStack(getData().getItem().get(),getCount());
    }

    public boolean isEmpty() {
        return this == EMPTY || this.getCount() <= 0 || data == UpgradeDatas.DUMMY;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Upgrade", UpgradeData.MAP.get(getData().getName()).toString());
        tag.putInt("Count",getCount());
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpgradeDataStack that)) return false;
        return getCount() == that.getCount() && getData().equals(that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData(), getCount());
    }
}
