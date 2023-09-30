package tfar.nabba.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

public class SearchHelper {

    public static final Map<Character, BiPredicate<ItemStack,String>> searchPredicate = new HashMap<>();

    public static final BiPredicate<ItemStack,String> DEFAULT = (stack, s) -> BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().startsWith(s);

    static {
        searchPredicate.put('$',((stack, s) -> {
            Item item = stack.getItem();
            List<TagKey<Item>> tags = item.builtInRegistryHolder().tags().toList();
            for (TagKey<Item> tag : tags) {
                if (tag.location().toString().contains(s)) {
                    return true;
                }
            }
            return false;
        }));
        searchPredicate.put('#',((stack, s) -> {
            Item item = stack.getItem();
            if (item instanceof EnchantedBookItem) {
                ListTag enchantmentTags = EnchantedBookItem.getEnchantments(stack);
                if (enchantmentTags.isEmpty()) return false;
                for (Tag tag : enchantmentTags) {
                    CompoundTag compoundTag = (CompoundTag) tag;
                    if (compoundTag.getString("id").contains(s)) return true;
                }
            }
            return false;
        }));
        searchPredicate.put('@',((stack, s) -> {
            Item item = stack.getItem();
            ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
            return location.getNamespace().contains(s);
        }));
    }
}
