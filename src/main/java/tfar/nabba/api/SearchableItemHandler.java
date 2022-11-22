package tfar.nabba.api;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public interface SearchableItemHandler extends ItemHandler {
    default List<ItemStack> getItemsForDisplay(int row, String search) {
        List<ItemStack> disp = new ArrayList<>();
        int countForDisplay = 0;
        int index = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            ItemStack stack = getStackInSlot(startPos + index).copy();//don't accidentally modify the stack!
            if (matches(stack,search)) {
                if (!merge(disp,stack)) {
                    countForDisplay++;
                }
            } else if (startPos+index >= getSlots()) {
                break;
            }
            index++;
        }
        return disp;
    }



    default boolean merge(List<ItemStack> stacks,ItemStack toMerge) {
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameTags(stack,toMerge)) {
                stack.grow(toMerge.getCount());
                return true;
            }
        }
        stacks.add(toMerge);
        return false;
    }

    default boolean matches(ItemStack stack, String search) {
        if (stack.isEmpty())return false;
        if (search.isEmpty()) {
            return true;
        } else {
            Item item = stack.getItem();
            if (search.startsWith("#")) {
                String sub = search.substring(1);

                List<TagKey<Item>> tags = item.builtInRegistryHolder().tags().toList();
                for (TagKey<Item> tag : tags) {
                    if (tag.location().getPath().startsWith(sub)) {
                        return true;
                    }
                }
                return false;
            } else if (Registry.ITEM.getKey(item).getPath().startsWith(search)) {
                return true;
            }
        }
        return false;
    }

    default int getFullItemSlots(String search) {
        int j = 0;
        for (int i = 0 ; i< getSlots();i++) {
            ItemStack stack = getStackInSlot(i);
            if (matches(stack,search)){
                i++;
            }
        }
        return j;
    }
}
