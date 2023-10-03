package tfar.nabba.inventory;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.util.FabricFluidStack;

public class FluidBarrelSlotWrapper extends SingleFluidStorage {

    private final FluidBarrelBlockEntity fluidBarrelBlockEntity;

    public FluidBarrelSlotWrapper(FluidBarrelBlockEntity fluidBarrelBlockEntity) {
        this.fluidBarrelBlockEntity = fluidBarrelBlockEntity;
        FluidBarrelBlockEntity.FluidBarrelHandler handler = fluidBarrelBlockEntity.getFluidHandler();
        FabricFluidStack fabricFluidStack = handler.getFluid();
        setVariantAndAmount(fabricFluidStack);
    }

    public void setVariantAndAmount(FabricFluidStack fluidStack) {
        variant = fluidStack.getFluidVariant();
        amount = fluidStack.getAmount();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return fluidBarrelBlockEntity.getFluidHandler().getTankCapacity(0);
    }


    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        fluidBarrelBlockEntity.getFluidHandler().setFluid(new FabricFluidStack(variant,amount));
        fluidBarrelBlockEntity.setChanged();
    }
}
