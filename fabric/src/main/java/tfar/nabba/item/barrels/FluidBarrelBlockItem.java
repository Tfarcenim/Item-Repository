package tfar.nabba.item.barrels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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
}
