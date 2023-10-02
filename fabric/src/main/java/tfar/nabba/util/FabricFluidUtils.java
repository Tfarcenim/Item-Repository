package tfar.nabba.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;

public class FabricFluidUtils {


    public static FabricFluidStack copyFluidStackWithSize(FabricFluidStack fluidStack, long droplets) {
        FabricFluidStack stack = fluidStack.copy();
        stack.setAmount(droplets);
        return stack;
    }

    public static void toBuf(FriendlyByteBuf buf,FluidVariant fluidVariant) {
        fluidVariant.toPacket(buf);
    }
    public static FluidVariant fromBuf(FriendlyByteBuf buf) {
        return FluidVariant.fromPacket(buf);
    }
}
