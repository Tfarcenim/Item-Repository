package tfar.nabba.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import tfar.nabba.shim.IFluidHandlerShim;
import tfar.nabba.util.FabricFluidStack;

import java.util.ArrayList;
import java.util.List;

public interface SearchableFluidHandler extends IFluidHandlerShim {
    default List<FabricFluidStack> getFluidDisplaySlots(int row, String search) {
        List<FabricFluidStack> disp = new ArrayList<>();
        int countForDisplay = 0;
        int index = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            FabricFluidStack stack = getFluidInTank(startPos + index);//don't accidentally modify the stack!
            if (matches(stack, search)) {
                if (!merge(disp, stack)) {
                    countForDisplay++;
                }
            } else if (startPos + index >= getTanks()) {
                break;
            }
            index++;
        }
        return disp;
    }

    default boolean merge(List<FabricFluidStack> stacks, FabricFluidStack toMerge) {
        for (FabricFluidStack stack : stacks) {
            if (stack.equals(toMerge)) {

                stack.grow(toMerge.getAmount());
                return true;
            }
        }
        stacks.add(toMerge);
        return false;
    }

    default FabricFluidStack requestFluid(FabricFluidStack requested, ItemStack container, Inventory playerInv, ServerPlayer player, boolean simulate) {
        for (int i = 0; i < getTanks();i++) {
            FabricFluidStack fluidStack = getFluidInTank(i);
            if (fluidStack.getFluidVariant().equals(requested.getFluidVariant())) {
                return null;//uidUtil.tryFillContainerAndStow(container, new SingleFluidSlotWrapper(this,i), playerInv, Integer.MAX_VALUE, player, !simulate);
            }
        }
        return null;//FluidActionResult.FAILURE;
    }

    default boolean matches(FabricFluidStack stack, String search) {
        if (stack.isEmpty()) return false;
        if (search.isEmpty()) {
            return true;
        } else {
            Fluid item = stack.getFluidVariant().getFluid();
            if (search.startsWith("#")) {
                String sub = search.substring(1);

                List<TagKey<Fluid>> tags = item.builtInRegistryHolder().tags().toList();
                for (TagKey<Fluid> tag : tags) {
                    if (tag.location().getPath().startsWith(sub)) {
                        return true;
                    }
                }
                return false;
            } else if (BuiltInRegistries.FLUID.getKey(item).getPath().startsWith(search)) {
                return true;
            }
        }
        return false;
    }

}
