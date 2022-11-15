package tfar.nabba.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;

public class ModBlockEntityTypes {
    public static final Block[] bb_blocks = new Block[]{ModBlocks.BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL
            ,ModBlocks.COPPER_BETTER_BARREL,ModBlocks.IRON_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,
            ModBlocks.GOLD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,ModBlocks.EMERALD_BETTER_BARREL,
            ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.CREATIVE_BETTER_BARREL};

    public static final Block[] ab_blocks = new Block[]{ModBlocks.ANTI_BARREL,ModBlocks.STONE_ANTI_BARREL
            ,ModBlocks.COPPER_ANTI_BARREL,ModBlocks.IRON_ANTI_BARREL,ModBlocks.LAPIS_ANTI_BARREL,
            ModBlocks.GOLD_ANTI_BARREL,ModBlocks.DIAMOND_ANTI_BARREL,ModBlocks.EMERALD_ANTI_BARREL,
            ModBlocks.NETHERITE_ANTI_BARREL,ModBlocks.CREATIVE_ANTI_BARREL};

    public static final BlockEntityType<AntiBarrelBlockEntity> ANTI_BARREL = BlockEntityType.Builder.of(Suppliers.REGULAR_AB,ab_blocks).build(null);
    public static final BlockEntityType<AntiBarrelBlockEntity> DISCRETE_ANTI_BARREL = BlockEntityType.Builder.of(Suppliers.DISCRETE_AB,ab_blocks).build(null);

    public static final BlockEntityType<BetterBarrelBlockEntity> BETTER_BARREL = BlockEntityType.Builder.of(Suppliers.REGULAR, bb_blocks).build(null);
    public static final BlockEntityType<BetterBarrelBlockEntity> DISCRETE_BETTER_BARREL =
            BlockEntityType.Builder.of(Suppliers.DISCRETE, bb_blocks).build(null);

    public static final BlockEntityType<ControllerBlockEntity> CONTROLLER =
            BlockEntityType.Builder.of(ControllerBlockEntity::create,ModBlocks.CONTROLLER).build(null);


    public static class Suppliers {
        public static final BlockEntityType.BlockEntitySupplier<BetterBarrelBlockEntity> REGULAR = BetterBarrelBlockEntity::create;
        public static final BlockEntityType.BlockEntitySupplier<BetterBarrelBlockEntity> DISCRETE = BetterBarrelBlockEntity::createDiscrete;
        public static final BlockEntityType.BlockEntitySupplier<AntiBarrelBlockEntity> REGULAR_AB = AntiBarrelBlockEntity::create;
        public static final BlockEntityType.BlockEntitySupplier<AntiBarrelBlockEntity> DISCRETE_AB = AntiBarrelBlockEntity::createDiscrete;
    }
}
