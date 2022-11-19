package tfar.nabba.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.block.*;
import tfar.nabba.util.BarrelFrameTiers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModBlocks {
    public static final AbstractBarrelBlock ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.WOOD);
    public static final AbstractBarrelBlock STONE_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.STONE);
    public static final AbstractBarrelBlock COPPER_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.COPPER);
    public static final AbstractBarrelBlock IRON_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.IRON);
    public static final AbstractBarrelBlock LAPIS_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.LAPIS);
    public static final AbstractBarrelBlock GOLD_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.GOLD);
    public static final AbstractBarrelBlock DIAMOND_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.DIAMOND);
    public static final AbstractBarrelBlock EMERALD_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.EMERALD);
    public static final AbstractBarrelBlock NETHERITE_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.NETHERITE);
    public static final AbstractBarrelBlock CREATIVE_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICKS), BarrelFrameTiers.CREATIVE);


    public static final AbstractBarrelBlock BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.WOOD);
    public static final AbstractBarrelBlock STONE_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.STONE);
    public static final AbstractBarrelBlock COPPER_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.COPPER);
    public static final AbstractBarrelBlock IRON_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.IRON);
    public static final AbstractBarrelBlock LAPIS_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.LAPIS);
    public static final AbstractBarrelBlock GOLD_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.GOLD);
    public static final AbstractBarrelBlock DIAMOND_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.DIAMOND);
    public static final AbstractBarrelBlock EMERALD_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.EMERALD);
    public static final AbstractBarrelBlock NETHERITE_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.NETHERITE);
    public static final AbstractBarrelBlock CREATIVE_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.CREATIVE);

    public static final AbstractBarrelBlock FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.WOOD);
    public static final AbstractBarrelBlock STONE_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.STONE);
    public static final AbstractBarrelBlock COPPER_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.COPPER);
    public static final AbstractBarrelBlock IRON_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.IRON);
    public static final AbstractBarrelBlock LAPIS_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.LAPIS);
    public static final AbstractBarrelBlock GOLD_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.GOLD);
    public static final AbstractBarrelBlock DIAMOND_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.DIAMOND);
    public static final AbstractBarrelBlock EMERALD_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.EMERALD);
    public static final AbstractBarrelBlock NETHERITE_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.NETHERITE);
    public static final AbstractBarrelBlock CREATIVE_FLUID_BARREL = new FluidBarrelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BarrelFrameTiers.CREATIVE);

    public static final Block CONTROLLER = new ControllerBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(3.5f));

    private static final List<Block> BLOCKS = new ArrayList<>();
    public static List<Block> getBlocks() {
        if (BLOCKS.isEmpty()) {
            for (Field field : ModBlocks.class.getFields()) {
                try {
                    Object o = field.get(null);
                    if (o instanceof Block block) BLOCKS.add(block);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
            }
        }
        return BLOCKS;
    }
}
