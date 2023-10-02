package tfar.nabba.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class FabricFluidStack {
    private static final FabricFluidStack EMPTY = new FabricFluidStack(FluidVariant.blank(),0);
    public static FabricFluidStack empty() {
        return EMPTY;
    }

    public static final String FLUID_VARIANT = "FluidVariant";
    public static final String AMOUNT = "amount";

    private final FluidVariant fluidVariant;
    private long amount;

    public FabricFluidStack(FluidVariant fluidVariant) {
        this(fluidVariant, FluidConstants.BUCKET);
    }

    public FabricFluidStack(FluidVariant fluidVariant,long amount) {
        this.fluidVariant = fluidVariant;
        this.amount = amount;
    }

    public FabricFluidStack(FabricFluidStack existing,long amount) {
        this.fluidVariant = existing.getFluidVariant();
        this.amount = amount;
    }

    public boolean isEmpty() {
        return this == empty() || amount <= 0 || fluidVariant.isBlank();
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(FLUID_VARIANT,fluidVariant.toNbt());
        tag.putLong(AMOUNT,amount);
        return tag;
    }

    public static FabricFluidStack of(CompoundTag tag) {
        CompoundTag fluidVariant = tag.getCompound(FLUID_VARIANT);
        long amount = tag.getLong(AMOUNT);
        return new FabricFluidStack(FluidVariant.fromNbt(fluidVariant),amount);
    }

    public void toPacket(FriendlyByteBuf buf) {
        fluidVariant.toPacket(buf);
        buf.writeLong(amount);
    }

    public static FabricFluidStack fromPacket(FriendlyByteBuf buf) {
        FluidVariant fluidVariant = FluidVariant.fromPacket(buf);
        long amount = buf.readLong();
        return new FabricFluidStack(fluidVariant,amount);
    }

    public FabricFluidStack copy() {
        if (isEmpty()) return empty();
        return new FabricFluidStack(FluidVariant.of(fluidVariant.getFluid(),fluidVariant.getNbt()),amount);
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public FluidVariant getFluidVariant() {
        return fluidVariant;
    }

    public void grow(long amount) {
        this.amount+=amount;
    }
    public void shrink(long amount) {
        grow(-amount);
    }

    Component getDisplayName() {
        return null;
    }

    public boolean sameFluid(FabricFluidStack other) {
        return this.fluidVariant == other.fluidVariant;
    }
}
