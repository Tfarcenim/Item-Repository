package tfar.nabba.api;

import net.minecraft.world.item.ItemStack;
import tfar.nabba.shim.IItemHandlerShim;
import tfar.nabba.util.CommonUtils;
import tfar.nabba.util.SearchHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface SearchableItemHandler extends IItemHandlerShim {
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

    default ItemStack requestItem(ItemStack stack) {
        ItemStack remaining = CommonUtils.copyStackWithSize(stack,Math.min(stack.getMaxStackSize(),stack.getCount()));
        ItemStack extracted = ItemStack.EMPTY;
        for (int i = 0; i < getSlots();i++) {
            ItemStack simExtract = extractItem(i,remaining.getCount(),true);
            if (!simExtract.isEmpty() && ItemStack.isSameItemSameTags(simExtract,remaining)) {
                extractItem(i,remaining.getCount(),false);
                if (extracted.isEmpty()) {
                    extracted = simExtract;
                } else {
                    extracted.grow(simExtract.getCount());
                }
                remaining.shrink(simExtract.getCount());
                if (remaining.isEmpty()) {
                    return extracted;
                }
            }
        }
        return extracted;
    }

    default ItemStack storeItem(ItemStack stack, boolean simulate) {
        if (isFull()) return stack;
        ItemStack remainder = stack;

        for (int i = 0; i < getSlots();i++) {
            remainder = insertItem(i,remainder,simulate);
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return remainder;
    }

    boolean isFull();

    default int getActualLimit() {
        return Integer.MAX_VALUE;
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
            String[] groups = search.split(" ");

            for (String subGroup : groups) {

                int len = subGroup.length();

                char firstPrefix = subGroup.charAt(0);

                boolean invert = firstPrefix == '-';

                if (invert && len > 1) {
                    firstPrefix = subGroup.charAt(1);
                }

                BiPredicate<ItemStack,String> searchPred = SearchHelper.searchPredicate.get(firstPrefix);
                boolean match = searchPred == null ? SearchHelper.DEFAULT.test(stack, invert? subGroup.substring(1) :subGroup) : searchPred.test(stack,invert ? subGroup.substring(2) : subGroup.substring(1));

                match = invert != match;

                if (!match) return false;
            }
        }
        return true;
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
