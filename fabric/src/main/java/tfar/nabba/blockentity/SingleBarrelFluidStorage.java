package tfar.nabba.blockentity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;

public class SingleBarrelFluidStorage extends SingleFluidStorage {

    private final FluidBarrelBlockEntity fluidBarrelBlockEntity;

    public SingleBarrelFluidStorage(FluidBarrelBlockEntity fluidBarrelBlockEntity) {

        this.fluidBarrelBlockEntity = fluidBarrelBlockEntity;
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return fluidBarrelBlockEntity.getFluidHandler().getTankCapacity(0);
    }
}
