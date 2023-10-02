package tfar.nabba.api;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.FabricFluidUtils;

import java.util.ArrayList;
import java.util.List;

public interface SearchableFluidHandler extends FluidHandler {
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

    default int getFullFluidSlots(String search) {
        int j = 0;
        for (int i = 0; i < getTanks(); i++) {
            @NotNull FabricFluidStack stack = getFluidInTank(i);
            if (matches(stack, search)) {
                i++;
            }
        }
        return j;
    }
}
