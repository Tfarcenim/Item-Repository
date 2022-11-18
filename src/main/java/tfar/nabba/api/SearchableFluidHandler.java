package tfar.nabba.api;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public interface SearchableFluidHandler extends FluidHandler {
    default List<Integer> getDisplaySlots(int row, String search) {
        List<Integer> disp = new ArrayList<>();
        int countForDisplay = 0;
        int index = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            FluidStack stack = getFluidInTank(startPos + index);
            if (matches(stack,search)) {
                disp.add(startPos + index);
                countForDisplay++;
            } else if (startPos+index >= getTanks()) {
                break;
            }
            index++;
        }
        return disp;
    }

    default boolean matches(FluidStack stack, String search) {
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
            } else if (Registry.FLUID.getKey(item).getPath().startsWith(search)) {
                return true;
            }
        }
        return false;
    }

    default int getFullSlots(String search) {
        int j = 0;
        for (int i = 0 ; i< getTanks();i++) {
            FluidStack stack = getFluidInTank(i);
            if (matches(stack,search)){
                i++;
            }
        }
        return j;
    }


}
