package tfar.nabba.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.NABBA;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.item.*;
import tfar.nabba.item.keys.*;
import tfar.nabba.util.BarrelFrameTiers;

public class ModItems {
    static CreativeModeTab tab = new CreativeModeTab(NABBA.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ANTI_BARREL);
        }
    };
    public static final Item ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.ANTI_BARREL,unstackable());
    public static final Item STONE_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.STONE_ANTI_BARREL,unstackable());
    public static final Item COPPER_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.COPPER_ANTI_BARREL,unstackable());
    public static final Item IRON_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.IRON_ANTI_BARREL,unstackable());
    public static final Item LAPIS_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.LAPIS_ANTI_BARREL,unstackable());
    public static final Item GOLD_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.GOLD_ANTI_BARREL,unstackable());
    public static final Item DIAMOND_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.DIAMOND_ANTI_BARREL,unstackable());
    public static final Item EMERALD_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.EMERALD_ANTI_BARREL,unstackable());
    public static final Item NETHERITE_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.NETHERITE_ANTI_BARREL,unstackable());
    public static final Item CREATIVE_ANTI_BARREL = new AntiBarrelBlockItem(ModBlocks.CREATIVE_ANTI_BARREL,unstackable());

    public static final Item BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.BETTER_BARREL,basic());
    public static final Item STONE_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.STONE_BETTER_BARREL,basic());
    public static final Item COPPER_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.COPPER_BETTER_BARREL,basic());
    public static final Item IRON_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.IRON_BETTER_BARREL,basic());
    public static final Item LAPIS_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.LAPIS_BETTER_BARREL,basic());
    public static final Item GOLD_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.GOLD_BETTER_BARREL,basic());
    public static final Item DIAMOND_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.DIAMOND_BETTER_BARREL,basic());
    public static final Item EMERALD_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.EMERALD_BETTER_BARREL,basic());
    public static final Item NETHERITE_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.NETHERITE_BETTER_BARREL,basic());
    public static final Item CREATIVE_BETTER_BARREL = new BetterBarrelBlockItem(ModBlocks.CREATIVE_BETTER_BARREL,basic());

    public static final Item FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.FLUID_BARREL,basic());
    public static final Item STONE_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.STONE_FLUID_BARREL,basic());
    public static final Item COPPER_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.COPPER_FLUID_BARREL,basic());
    public static final Item IRON_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.IRON_FLUID_BARREL,basic());
    public static final Item LAPIS_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.LAPIS_FLUID_BARREL,basic());
    public static final Item GOLD_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.GOLD_FLUID_BARREL,basic());
    public static final Item DIAMOND_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.DIAMOND_FLUID_BARREL,basic());
    public static final Item EMERALD_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.EMERALD_FLUID_BARREL,basic());
    public static final Item NETHERITE_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.NETHERITE_FLUID_BARREL,basic());
    public static final Item CREATIVE_FLUID_BARREL = new FluidBarrelBlockItem(ModBlocks.CREATIVE_FLUID_BARREL,basic());

    public static final Item BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.STORAGE);
    public static final Item x4_BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.x4_STORAGE);
    public static final Item x16_BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.x16_STORAGE);
    public static final Item x64_BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.x64_STORAGE);
    public static final Item x256_BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.x256_STORAGE);
    public static final Item x1024_BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.x1024_STORAGE);
    public static final Item INFINITE_BETTER_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.better(basic(), UpgradeStack.INFINITE_STORAGE);


    public static final Item ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.STORAGE);
    public static final Item x4_ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.x4_STORAGE);
    public static final Item x16_ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.x16_STORAGE);
    public static final Item x64_ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.x64_STORAGE);
    public static final Item x256_ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.x256_STORAGE);
    public static final Item x1024_ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.x1024_STORAGE);
    public static final Item INFINITE_ANTI_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.anti(basic(), UpgradeStack.INFINITE_STORAGE);



    public static final Item FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.STORAGE);
    public static final Item x4_FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.x4_STORAGE);
    public static final Item x16_FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.x16_STORAGE);
    public static final Item x64_FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.x64_STORAGE);
    public static final Item x256_FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.x256_STORAGE);
    public static final Item x1024_FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.x1024_STORAGE);
    public static final Item INFINITE_FLUID_BARREL_STORAGE_UPGRADE = StorageUpgradeItem.fluid(basic(), UpgradeStack.INFINITE_STORAGE);


    public static final Item INFINITE_VENDING_UPGRADE = new UpgradeItem(basic(), UpgradeStack.INFINITE_VENDING);
    public static final Item VOID_UPGRADE = new UpgradeItem(basic(), UpgradeStack.VOID);
    public static final Item PICKUP_1x1_UPGRADE = new UpgradeItem(basic(), UpgradeStack.PICKUP_1x1);
    public static final Item PICKUP_3x3_UPGRADE = new UpgradeItem(basic(), UpgradeStack.PICKUP_3x3);
    public static final Item PICKUP_9x9_UPGRADE = new UpgradeItem(basic(), UpgradeStack.PICKUP_9x9);

    public static final Item KEY_RING = new KeyRingItem(basic());
    public static final Item HIDE_KEY = new BlockStateKeyItem(basic(),BetterBarrelBlock.DISCRETE);
    public static final Item LOCK_KEY = new BlockStateKeyItem(basic(), BetterBarrelBlock.LOCKED);
    public static final Item CONTROLLER_KEY = new ControllerKeyItem(basic());

    public static final Item CONNECT_KEY = new BlockStateKeyItem(basic(),BetterBarrelBlock.CONNECTED);
    public static final Item FLUID_CONTROLLER_KEY = new FluidControllerKeyItem(basic());

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

    public static final Item CONTROLLER = new BlockItem(ModBlocks.CONTROLLER,basic());
    public static final Item BARREL_INTERFACE = new BlockItem(ModBlocks.BARREL_INTERFACE,basic());

    private static Item.Properties basic() {
        return new Item.Properties().tab(tab);
    }

    private static Item.Properties unstackable() {
        return new Item.Properties().tab(tab).stacksTo(1);
    }
}
