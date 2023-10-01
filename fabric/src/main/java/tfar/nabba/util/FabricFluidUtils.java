package tfar.nabba.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;

public class FabricFluidUtils {

    public static FluidVariant copyFluidVariant(FluidVariant source) {
        Fluid fluid = source.getFluid();
        CompoundTag tag = source.getNbt();
        return FluidVariant.of(fluid,tag);
    }

    public static FluidVariant createFluidVariantWithAmount(Fluid fluid,long millibuckets) {
        long amount = 81 * millibuckets;//fabric uses 81000 for bucket denominator
        CompoundTag tag = new CompoundTag();
        tag.putLong("amount",amount);
        return FluidVariant.of(fluid,tag);
    }

    public static void toBuf(FriendlyByteBuf buf,FluidVariant fluidVariant) {
        fluidVariant.toPacket(buf);
    }
    public static FluidVariant fromBuf(FriendlyByteBuf buf) {
        return FluidVariant.fromPacket(buf);
    }
}
