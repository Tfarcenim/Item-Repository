package tfar.nabba.blockentity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import tfar.nabba.util.FabricFluidStack;

public class SingleBarrelFluidStorage extends SingleFluidStorage {

    private final FluidBarrelBlockEntity fluidBarrelBlockEntity;

    public SingleBarrelFluidStorage(FluidBarrelBlockEntity fluidBarrelBlockEntity) {
        this.fluidBarrelBlockEntity = fluidBarrelBlockEntity;
        FluidBarrelBlockEntity.FluidBarrelHandler handler = fluidBarrelBlockEntity.getFluidHandler();
        FabricFluidStack fabricFluidStack = handler.getFluid();
        this.variant = fabricFluidStack.getFluidVariant();
        this.amount = fabricFluidStack.getAmount();//the fluid is initially blank
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
