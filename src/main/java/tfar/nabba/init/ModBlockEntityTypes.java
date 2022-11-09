package tfar.nabba.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

public class ModBlockEntityTypes {
    public static final BlockEntityType<AntiBarrelBlockEntity> REPOSITORY = BlockEntityType.Builder.of(AntiBarrelBlockEntity::new,ModBlocks.ANTI_BARREL).build(null);
    public static final BlockEntityType<BetterBarrelBlockEntity> BETTER_BARREL =
            BlockEntityType.Builder.of(Suppliers.WOOD,ModBlocks.BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL
                    ,ModBlocks.COPPER_BETTER_BARREL,ModBlocks.IRON_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,
                    ModBlocks.GOLD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,ModBlocks.EMERALD_BETTER_BARREL,
                    ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.CREATIVE_BETTER_BARREL).build(null);
    public static class Suppliers {
        public static final BlockEntityType.BlockEntitySupplier<BetterBarrelBlockEntity> WOOD = BetterBarrelBlockEntity::create;
    }
}
