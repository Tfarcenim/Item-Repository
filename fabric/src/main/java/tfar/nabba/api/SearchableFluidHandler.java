package tfar.nabba.api;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public interface SearchableFluidHandler extends FluidHandler {
    default List<FluidStack> getFluidDisplaySlots(int row, String search) {
        List<FluidStack> disp = new ArrayList<>();
        int countForDisplay = 0;
        int index = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            FluidVariant stack = getFluidInTank(startPos + index).copy();//don't accidentally modify the stack!
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

    default boolean merge(List<FluidStack> stacks, FluidStack toMerge) {
        for (FluidVariant stack : stacks) {
            if (stack.isFluidEqual(toMerge)) {
                stack.grow(toMerge.getAmount());
                return true;
            }
        }
        stacks.add(toMerge);
        return false;
    }

    default boolean matches(FluidVariant stack, String search) {
        if (stack.isBlank()) return false;
        if (search.isEmpty()) {
            return true;
        } else {
            Fluid item = stack.getFluid();
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

    default int getFullFluidSlots(String search) {
        int j = 0;
        for (int i = 0; i < getTanks(); i++) {
            FluidStack stack = getFluidInTank(i);
            if (matches(stack, search)) {
                i++;
            }
        }
        return j;
    }
}
