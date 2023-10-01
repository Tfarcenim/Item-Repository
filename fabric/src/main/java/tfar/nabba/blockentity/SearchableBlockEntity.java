package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SearchableBlockEntity extends BlockEntity implements HasSearchBar {

    protected String search = "";
    protected ContainerData itemDataAccess;
    protected ContainerData fluidDataAccess;

    protected final int[] syncSlots = new int[54];

    protected final ContainerData syncSlotsAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return syncSlots[pIndex];
        }

        @Override
        public void set(int pIndex, int pValue) {
            syncSlots[pIndex] = pValue;
        }

        @Override
        public int getCount() {
            return syncSlots.length;
        }
    };


    public SearchableBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public void setSearchString(String search) {
        this.search = search;
    }

    @Override
    public String getSearchString() {
        return search;
    }

    public ContainerData getItemDataAccess() {
        return itemDataAccess;
    }

    public ContainerData getFluidDataAccess() {
        return fluidDataAccess;
    }

    public ContainerData getSyncSlotsAccess() {
        return syncSlotsAccess;
    }
}
