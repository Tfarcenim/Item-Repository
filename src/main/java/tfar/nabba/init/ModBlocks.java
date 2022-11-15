package tfar.nabba.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.block.AntiBarrelBlock;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.block.ControllerBlock;
import tfar.nabba.util.BarrelFrameTiers;

public class ModBlocks {
    public static final AbstractBarrelBlock ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.of(Material.METAL), BarrelFrameTiers.WOOD);
    public static final AbstractBarrelBlock STONE_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.of(Material.METAL), BarrelFrameTiers.STONE);
    public static final AbstractBarrelBlock COPPER_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.of(Material.METAL), BarrelFrameTiers.COPPER);
    public static final AbstractBarrelBlock IRON_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.of(Material.METAL), BarrelFrameTiers.IRON);
    public static final AbstractBarrelBlock LAPIS_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.LAPIS);
    public static final AbstractBarrelBlock GOLD_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.GOLD);
    public static final AbstractBarrelBlock DIAMOND_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.DIAMOND);
    public static final AbstractBarrelBlock EMERALD_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.EMERALD);
    public static final AbstractBarrelBlock NETHERITE_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.NETHERITE);
    public static final AbstractBarrelBlock CREATIVE_ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.CREATIVE);


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
    public static final Block CONTROLLER = new ControllerBlock(BlockBehaviour.Properties.of(Material.METAL));
}
