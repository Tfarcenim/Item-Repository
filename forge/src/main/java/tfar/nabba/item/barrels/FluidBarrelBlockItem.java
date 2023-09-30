package tfar.nabba.item.barrels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.capability.FluidBarrelItemStackItemHandler;
import tfar.nabba.inventory.tooltip.FluidBarrelTooltip;
import tfar.nabba.util.BlockItemBarrelUtils;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.ForgeUtils;

import java.util.Optional;

public class FluidBarrelBlockItem extends BlockItem {
    public FluidBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public static void setFluid(ItemStack container, FluidStack copyStackWithSize) {
        CompoundTag tag = copyStackWithSize.writeToNBT(new CompoundTag());
        BlockItemBarrelUtils.getOrCreateBlockEntityTag(container).put(NBTKeys.Stack.name(), tag);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBarrelItemStackItemHandler(stack);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        FluidStack disp = getStoredFluid(stack);
        return disp.isEmpty() ? super.getTooltipImage(stack) : Optional.of(new FluidBarrelTooltip(disp));
    }

    public static FluidStack getStoredFluid(ItemStack barrel) {
        if (barrel.getTagElement(BlockItem.BLOCK_ENTITY_TAG) != null) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound(NBTKeys.Stack.name()));
            return stack;
        }
        return FluidStack.EMPTY;
    }

    public static boolean isFluidValid(ItemStack barrel,FluidStack stack) {
        if (!barrel.hasTag()) return true;
        FluidStack existing = getStoredFluid(barrel);
        FluidStack ghost = getGhost(barrel);
        return ForgeUtils.isFluidValid(existing,stack,ghost);
    }

    public static FluidStack getGhost(ItemStack barrel) {
        if (BlockItemBarrelUtils.getBlockEntityTag(barrel)!=null) {
            return FluidStack.loadFluidStackFromNBT(BlockItemBarrelUtils.getBlockEntityTag(barrel).getCompound(NBTKeys.Ghost.name()));
        }
        return FluidStack.EMPTY;
    }

}
