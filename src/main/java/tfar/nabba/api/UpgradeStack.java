package tfar.nabba.api;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Upgrades;

import java.util.Objects;

public class UpgradeStack {
    public static final UpgradeStack EMPTY = defaultInstance(Upgrades.DUMMY).setImmutable(true);
    public static final UpgradeStack STORAGE = defaultInstance(Upgrades.STORAGE).setImmutable(true);
    public static final UpgradeStack x4_STORAGE = new UpgradeStack(Upgrades.STORAGE,4).setImmutable(true);
    public static final UpgradeStack x16_STORAGE = new UpgradeStack(Upgrades.STORAGE,16).setImmutable(true);
    public static final UpgradeStack x64_STORAGE = new UpgradeStack(Upgrades.STORAGE,64).setImmutable(true);
    public static final UpgradeStack x256_STORAGE = new UpgradeStack(Upgrades.STORAGE,256).setImmutable(true);
    public static final UpgradeStack x1024_STORAGE = new UpgradeStack(Upgrades.STORAGE,1024).setImmutable(true);
    public static final UpgradeStack INFINITE_STORAGE = new UpgradeStack(Upgrades.STORAGE,64000).setImmutable(true);

    public static final UpgradeStack INFINITE_VENDING = defaultInstance(Upgrades.INFINITE_VENDING).setImmutable(true);

    public static final UpgradeStack STORAGE_DOWNGRADE = defaultInstance(Upgrades.STORAGE_DOWNGRADE).setImmutable(true);
    public static final UpgradeStack REDSTONE = defaultInstance(Upgrades.REDSTONE).setImmutable(true);
    public static final UpgradeStack VOID = defaultInstance(Upgrades.VOID).setImmutable(true);

    public static final UpgradeStack PICKUP_1x1 = defaultInstance(Upgrades.PICKUP).setImmutable(true);
    public static final UpgradeStack PICKUP_3x3 = new UpgradeStack(Upgrades.PICKUP,3).setImmutable(true);
    public static final UpgradeStack PICKUP_9x9 = new UpgradeStack(Upgrades.PICKUP,9).setImmutable(true);

    private final Upgrade data;
    private int count;
    private boolean immutable;

    public static UpgradeStack defaultInstance(Upgrade upgrade) {
        return new UpgradeStack(upgrade);
    }

    public static UpgradeStack of(CompoundTag tag) {
        ResourceLocation name = new ResourceLocation(tag.getString("Upgrade"));
        return new UpgradeStack(Upgrade.REGISTRY.get(name),tag.getInt("Count"));
    }

    public UpgradeStack(Upgrade data) {
        this(data,1);
    }
    public UpgradeStack(Upgrade data, int count) {
        this.data = data;
        this.count = count;
    }

    public UpgradeStack setImmutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    public Upgrade getData() {
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

    public int getStorageMultiplier() {
        return getCount() * getData().getStorageMultiplier();
    }

    public UpgradeStack copy() {
        return new UpgradeStack(getData(),getCount());
    }

    public ItemStack createItemStack() {
        return new ItemStack(getData().getItem().get(),getCount());
    }

    public boolean isEmpty() {
        return this == EMPTY || this.getCount() <= 0 || data == Upgrades.DUMMY;
    }


    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString(NBTKeys.Upgrade.name(), getData().getKey().toString());
        tag.putInt(NBTKeys.Count.name(), getCount());
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpgradeStack that)) return false;
        return getCount() == that.getCount() && getData().equals(that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData(), getCount());
    }

    public MutableComponent getName() {
        return Component.literal(getCount() +" ").withStyle(ChatFormatting.AQUA).append(Component.translatable(getData().getDescriptionId()));
    }
}
