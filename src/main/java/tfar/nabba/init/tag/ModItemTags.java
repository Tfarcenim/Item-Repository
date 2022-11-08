package tfar.nabba.init.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import tfar.nabba.NABBA;

public class ModItemTags {
    public static final TagKey<Item> BETTER_BARRELS = ItemTags.create(new ResourceLocation(NABBA.MODID,"better_barrels"));
    public static final TagKey<Item> ANTI_BARRELS = ItemTags.create(new ResourceLocation(NABBA.MODID,"anti_barrels"));
}
