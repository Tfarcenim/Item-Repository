package tfar.nabba.api;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.inventory.SingleFluidSlotWrapper;
import tfar.nabba.shim.IFluidHandlerShim;

public interface FluidHandler extends IFluidHandlerShim {

    int fill(int tank,FluidVariant stack,FluidAction action);


    @NotNull FluidVariant drain(int tank, FluidVariant resource, FluidAction action);

    @NotNull FluidVariant drain(int tank, int maxDrain, FluidAction action);

    default FluidActionResult requestFluid(FluidVariant requested, ItemStack container, IItemHandler playerInv, ServerPlayer player, boolean simulate) {
        for (int i = 0; i < getTanks();i++) {
            FluidVariant FluidVariant = getFluidInTank(i);
            if (FluidVariant.isFluidEqual(requested)) {
                return FluidUtil.tryFillContainerAndStow(container, new SingleFluidSlotWrapper(this,i), playerInv, Integer.MAX_VALUE, player, !simulate);
            }
        }
        return FluidActionResult.FAILURE;
    }
    default FluidActionResult storeFluid(ItemStack container, IItemHandler playerInv, ServerPlayer player, boolean simulate) {
        return FluidUtil.tryEmptyContainerAndStow(container,this,playerInv,Integer.MAX_VALUE,player,!simulate);
    }

    default FluidActionResult fillTankWithContainer(int tank, ItemStack container, IItemHandler playerInv, ServerPlayer player, boolean simulate) {
        return FluidUtil.tryEmptyContainerAndStow(container,new SingleFluidSlotWrapper(this,tank),playerInv,Integer.MAX_VALUE,player,!simulate);
    }
}
