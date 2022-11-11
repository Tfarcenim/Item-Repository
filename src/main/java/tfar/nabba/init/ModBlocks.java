package tfar.nabba.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import tfar.nabba.block.AntiBarrelBlock;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.block.ControllerBlock;
import tfar.nabba.util.BarrelFrameTiers;

public class ModBlocks {
    public static final Block ANTI_BARREL = new AntiBarrelBlock(BlockBehaviour.Properties.of(Material.METAL));
    public static final Block BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.WOOD);
    public static final Block STONE_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.STONE);
    public static final Block COPPER_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.COPPER);
    public static final Block IRON_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.IRON);
    public static final Block LAPIS_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.LAPIS);
    public static final Block GOLD_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.GOLD);
    public static final Block DIAMOND_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.DIAMOND);
    public static final Block EMERALD_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.EMERALD);
    public static final Block NETHERITE_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.NETHERITE);
    public static final Block CREATIVE_BETTER_BARREL = new BetterBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL), BarrelFrameTiers.CREATIVE);
    public static final Block CONTROLLER = new ControllerBlock(BlockBehaviour.Properties.of(Material.METAL));
}
