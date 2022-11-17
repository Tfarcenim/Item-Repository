package tfar.nabba.api;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface SearchableItemHandler extends ItemHandler {
    default List<Integer> getDisplaySlots(int row, String search) {
        List<Integer> disp = new ArrayList<>();
        int countForDisplay = 0;
        int index = 0;
        int startPos = 9 * row;
        while (countForDisplay < 54) {
            ItemStack stack = getStackInSlot(startPos + index);
            if (matches(stack,search)) {
                disp.add(startPos + index);
                countForDisplay++;
            } else if (startPos+index >= getSlots()) {
                break;
            }
            index++;
        }
        return disp;
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

}
