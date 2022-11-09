package tfar.nabba.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.NABBA;
import tfar.nabba.item.KeyItem;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.item.VanityKeyItem;
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

    public static final Item STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.x1_STORAGE);
    public static final Item x4_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.x4_STORAGE);
    public static final Item x16_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.x16_STORAGE);
    public static final Item x64_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.x64_STORAGE);
    public static final Item x256_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.x256_STORAGE);
    public static final Item x1024_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.x1024_STORAGE);
    public static final Item INFINITE_STORAGE_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.INFINITE_STORAGE);
    public static final Item INFINITE_VENDING_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.INFINITE_VENDING);
    public static final Item VOID_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.VOID);
    public static final Item PICKUP_3x3_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.PICKUP_3x3);
    public static final Item PICKUP_9x9_UPGRADE = new UpgradeItem(basic(), UpgradeDatas.PICKUP_9x9);

    public static final Item KEY_RING = new Item(basic());
    public static final Item HIDE_KEY = new KeyItem(basic());
    public static final Item LOCK_KEY = new KeyItem(basic());
    public static final Item VANITY_KEY = new VanityKeyItem(basic());

    private static Item.Properties basic() {
        return new Item.Properties().tab(tab);
    }
}
