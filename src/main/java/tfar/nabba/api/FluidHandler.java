package tfar.nabba.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.inventory.SingleFluidSlotWrapper;

public interface FluidHandler extends IFluidHandler {
    int fill(int tank,int amount,FluidAction action);

    int fill(int tank,FluidStack stack,FluidAction action);


    @NotNull FluidStack drain(int tank, FluidStack resource, FluidAction action);

    @NotNull FluidStack drain(int tank, int maxDrain, FluidAction action);

    FluidActionResult attemptDrainTankWithContainer(int tank, ItemStack container,IItemHandler playerInv,ServerPlayer player, boolean b);
    default FluidActionResult fillTanksWithContainer(ItemStack container, IItemHandler playerInv,ServerPlayer player,boolean simulate) {
        return FluidUtil.tryEmptyContainerAndStow(container,this,playerInv,Integer.MAX_VALUE,player,simulate);
    }

    default FluidActionResult fillTankWithContainer(int tank, ItemStack container, IItemHandler playerInv, ServerPlayer player, boolean simulate) {
        return FluidUtil.tryEmptyContainerAndStow(container,new SingleFluidSlotWrapper(this,tank),playerInv,Integer.MAX_VALUE,player,simulate);
    }
}
