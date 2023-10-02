package tfar.nabba.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.util.FabricFluidStack;

public interface FluidHandler extends IFluidHandlerShim {

    long fill(int tank, FabricFluidStack stack, FluidAction action);


    @NotNull FabricFluidStack drain(int tank, FabricFluidStack resource, FluidAction action);

    @NotNull FabricFluidStack drain(int tank, long maxDrain, FluidAction action);

    default FabricFluidStack requestFluid(FabricFluidStack requested, ItemStack container, Inventory playerInv, ServerPlayer player, boolean simulate) {
        for (int i = 0; i < getTanks();i++) {
            FabricFluidStack fluidStack = getFluidInTank(i);
            if (fluidStack.getFluidVariant().equals(requested.getFluidVariant())) {
                return null;//uidUtil.tryFillContainerAndStow(container, new SingleFluidSlotWrapper(this,i), playerInv, Integer.MAX_VALUE, player, !simulate);
            }
        }
        return null;//FluidActionResult.FAILURE;
    }
}
