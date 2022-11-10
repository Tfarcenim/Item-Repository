package tfar.nabba.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.NABBA;
import tfar.nabba.api.UpgradeDataStack;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.item.*;
import tfar.nabba.util.BarrelFrameTiers;
import tfar.nabba.util.UpgradeDatas;

public class ModItems {
    static CreativeModeTab tab = new CreativeModeTab(NABBA.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ANTI_BARREL);
        }
    };
    public static final Item ANTI_BARREL = new BlockItem(ModBlocks.ANTI_BARREL,basic());
    public static final Item BETTER_BARREL = new BlockItem(ModBlocks.BETTER_BARREL,basic());
    public static final Item STONE_BETTER_BARREL = new BlockItem(ModBlocks.STONE_BETTER_BARREL,basic());
    public static final Item COPPER_BETTER_BARREL = new BlockItem(ModBlocks.COPPER_BETTER_BARREL,basic());
    public static final Item IRON_BETTER_BARREL = new BlockItem(ModBlocks.IRON_BETTER_BARREL,basic());
    public static final Item LAPIS_BETTER_BARREL = new BlockItem(ModBlocks.LAPIS_BETTER_BARREL,basic());
    public static final Item GOLD_BETTER_BARREL = new BlockItem(ModBlocks.GOLD_BETTER_BARREL,basic());
    public static final Item DIAMOND_BETTER_BARREL = new BlockItem(ModBlocks.DIAMOND_BETTER_BARREL,basic());
    public static final Item EMERALD_BETTER_BARREL = new BlockItem(ModBlocks.EMERALD_BETTER_BARREL,basic());
    public static final Item NETHERITE_BETTER_BARREL = new BlockItem(ModBlocks.NETHERITE_BETTER_BARREL,basic());
    public static final Item CREATIVE_BETTER_BARREL = new BlockItem(ModBlocks.CREATIVE_BETTER_BARREL,basic());

    public static final Item STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.STORAGE);
    public static final Item x4_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.x4_STORAGE);
    public static final Item x16_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.x16_STORAGE);
    public static final Item x64_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.x64_STORAGE);
    public static final Item x256_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.x256_STORAGE);
    public static final Item x1024_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.x1024_STORAGE);
    public static final Item INFINITE_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.INFINITE_STORAGE);
    public static final Item INFINITE_VENDING_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.INFINITE_VENDING);
    public static final Item VOID_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.VOID);
    public static final Item PICKUP_1x1_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.PICKUP_1x1);
    public static final Item PICKUP_3x3_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.PICKUP_3x3);
    public static final Item PICKUP_9x9_UPGRADE = new UpgradeItem(basic(), UpgradeDataStack.PICKUP_9x9);

    public static final Item KEY_RING = new KeyRingItem(basic());
    public static final Item HIDE_KEY = new BlockStateKeyItem(basic(),BetterBarrelBlock.DISCRETE);
    public static final Item LOCK_KEY = new BlockStateKeyItem(basic(), BetterBarrelBlock.LOCKED);
    public static final Item VANITY_KEY = new VanityKeyItem(basic());
    public static final Item WOOD_TO_STONE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.STONE);
    public static final Item STONE_TO_COPPER_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.COPPER);
    public static final Item COPPER_TO_IRON_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.IRON);
    public static final Item IRON_TO_LAPIS_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.IRON,BarrelFrameTiers.LAPIS);
    public static final Item LAPIS_TO_GOLD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.LAPIS,BarrelFrameTiers.GOLD);
    public static final Item GOLD_TO_DIAMOND_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.GOLD,BarrelFrameTiers.DIAMOND);
    public static final Item DIAMOND_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.DIAMOND,BarrelFrameTiers.EMERALD);
    public static final Item EMERALD_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.EMERALD,BarrelFrameTiers.NETHERITE);
    public static final Item NETHERITE_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.NETHERITE,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_COPPER_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.COPPER);
    public static final Item STONE_TO_IRON_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.IRON);
    public static final Item COPPER_TO_LAPIS_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.LAPIS);
    public static final Item IRON_TO_GOLD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.IRON,BarrelFrameTiers.GOLD);
    public static final Item LAPIS_TO_DIAMOND_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.LAPIS,BarrelFrameTiers.DIAMOND);
    public static final Item GOLD_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.GOLD,BarrelFrameTiers.EMERALD);
    public static final Item DIAMOND_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.DIAMOND,BarrelFrameTiers.NETHERITE);
    public static final Item EMERALD_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.EMERALD,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_IRON_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.IRON);
    public static final Item STONE_TO_LAPIS_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.LAPIS);
    public static final Item COPPER_TO_GOLD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.GOLD);
    public static final Item IRON_TO_DIAMOND_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.IRON,BarrelFrameTiers.DIAMOND);
    public static final Item LAPIS_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.LAPIS,BarrelFrameTiers.EMERALD);
    public static final Item GOLD_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.GOLD,BarrelFrameTiers.NETHERITE);
    public static final Item DIAMOND_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.DIAMOND,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_LAPIS_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.LAPIS);
    public static final Item STONE_TO_GOLD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.GOLD);
    public static final Item COPPER_TO_DIAMOND_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.DIAMOND);
    public static final Item IRON_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.IRON,BarrelFrameTiers.EMERALD);
    public static final Item LAPIS_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.LAPIS,BarrelFrameTiers.NETHERITE);
    public static final Item GOLD_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.GOLD,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_GOLD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.GOLD);
    public static final Item STONE_TO_DIAMOND_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.DIAMOND);
    public static final Item COPPER_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.EMERALD);
    public static final Item IRON_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.IRON,BarrelFrameTiers.NETHERITE);
    public static final Item LAPIS_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.LAPIS,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_DIAMOND_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.DIAMOND);
    public static final Item STONE_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.EMERALD);
    public static final Item COPPER_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.NETHERITE);
    public static final Item IRON_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.IRON,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_EMERALD_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.EMERALD);
    public static final Item STONE_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.NETHERITE);
    public static final Item COPPER_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.COPPER,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_NETHERITE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.NETHERITE);
    public static final Item STONE_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.STONE,BarrelFrameTiers.CREATIVE);


    public static final Item WOOD_TO_CREATIVE_FRAME_UPGRADE = new BarrelFrameUpgradeItem(basic(), BarrelFrameTiers.WOOD,BarrelFrameTiers.CREATIVE);


    private static Item.Properties basic() {
        return new Item.Properties().tab(tab);
    }
}
