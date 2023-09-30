package tfar.nabba.init.tag;

import net.minecraft.core.registries.Registries;
import tfar.nabba.NABBA;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> BARRELS = bind(new ResourceLocation(NABBA.MODID,"barrels"));
    public static final TagKey<Item> BETTER_BARRELS = bind(new ResourceLocation(NABBA.MODID,"better_barrels"));
    public static final TagKey<Item> ANTI_BARRELS = bind(new ResourceLocation(NABBA.MODID,"anti_barrels"));
    public static final TagKey<Item> FLUID_BARRELS = bind(new ResourceLocation(NABBA.MODID,"fluid_barrels"));
    public static final TagKey<Item> KEYS = bind(new ResourceLocation(NABBA.MODID,"keys"));
    public static final TagKey<Item> NETHER_BRICKS = bind(new ResourceLocation("forge","blocks/nether_bricks"));
    public static final TagKey<Item> NETHER_BRICK_SLABS = bind(new ResourceLocation("forge","blocks/nether_brick_slabs"));

    private static TagKey<Item> bind(ResourceLocation $$0) {
        return TagKey.create(Registries.ITEM, $$0);
    }

}
