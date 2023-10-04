package tfar.nabba.item.barrels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import tfar.nabba.NABBAFabric;
import tfar.nabba.inventory.tooltip.FluidBarrelTooltip;
import tfar.nabba.util.BlockItemBarrelUtils;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.FabricUtils;
import tfar.nabba.util.NBTKeys;

import java.util.Optional;

public class FluidBarrelBlockItem extends BlockItem {
    public FluidBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public static void setFluid(ItemStack container, FabricFluidStack copyStackWithSize) {
        CompoundTag tag = copyStackWithSize.toTag();
        BlockItemBarrelUtils.getOrCreateBlockEntityTag(container).put(NBTKeys.Stack.name(), tag);
    }


    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        FabricFluidStack disp = getStoredFluid(stack);
        return disp.isEmpty() ? super.getTooltipImage(stack) : Optional.of(new FluidBarrelTooltip(disp));
    }

    public static FabricFluidStack getStoredFluid(ItemStack barrel) {
        if (barrel.getTagElement(BlockItem.BLOCK_ENTITY_TAG) != null) {
            FabricFluidStack stack = FabricFluidStack.of(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound(NBTKeys.Stack.name()));
            return stack;
        }
        return FabricFluidStack.empty();
    }

    public static boolean isFluidValid(ItemStack barrel,FabricFluidStack stack) {
        if (!barrel.hasTag()) return true;
        FabricFluidStack existing = getStoredFluid(barrel);
        FabricFluidStack ghost = getGhost(barrel);
        return FabricUtils.isFluidValid(existing,stack,ghost);
    }

    public static FabricFluidStack getGhost(ItemStack barrel) {
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel)!=null) {
            return FabricFluidStack.of(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound(NBTKeys.Ghost.name()));
        }
        return FabricFluidStack.empty();
    }

    public static SingleItemBarrelWrapper getStorage(ItemStack stack) {
        if (stack.getItem() instanceof FluidBarrelBlockItem) {
            return new SingleItemBarrelWrapper(stack);
        }
        return null;
    }

    public static class SingleItemBarrelWrapper extends SingleFluidStackStorage {

        private final ItemStack barrel;

        private FabricFluidStack lastReleasedSnapshot = null;


        public SingleItemBarrelWrapper(ItemStack barrel) {
            this.barrel = barrel;
        }

        @Override
        protected FabricFluidStack getStack() {
            return getStoredFluid(barrel);
        }

        @Override
        protected void setStack(FabricFluidStack stack) {
            FluidBarrelBlockItem.setFluid(barrel,stack);
        }

        @Override
        public long getCapacity() {
            return BetterBarrelBlockItem.getStorageMultiplier(barrel) * 1000L *
                    (BetterBarrelBlockItem.storageDowngrade(barrel) ? 1L : NABBAFabric.ServerCfg.fluid_barrel_base_storage);
        }


//        @Override
        //       public void updateSnapshots(TransactionContext transaction) {
        //           storage.setChanged();
        //           super.updateSnapshots(transaction);
        //       }

        @Override
        protected void releaseSnapshot(FabricFluidStack snapshot) {
            lastReleasedSnapshot = snapshot;
        }

        @Override
        protected void onFinalCommit() {
            // Try to apply the change to the original stack
            FabricFluidStack original = lastReleasedSnapshot;
            FabricFluidStack currentStack = getStack();

            if (!original.isEmpty() && original.sameFluid(currentStack)) {
                // None is empty and the items match: just update the amount and NBT, and reuse the original stack.
                setStack(currentStack.copy());
            } else {
                // Otherwise assume everything was taken from original so empty it.
                original.setAmount(0);
            }
        }
    }

}
