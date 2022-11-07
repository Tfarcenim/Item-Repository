package tfar.itemrepository.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.itemrepository.blockentity.BetterBarrelBlockEntity;
import tfar.itemrepository.blockentity.RepositoryBlockEntity;

public class ModBlockEntityTypes {
    public static final BlockEntityType<RepositoryBlockEntity> REPOSITORY = BlockEntityType.Builder.of(RepositoryBlockEntity::new,ModBlocks.REPOSITORY).build(null);
    public static final BlockEntityType<BetterBarrelBlockEntity> BETTER_BARREL =
            BlockEntityType.Builder.of(Suppliers.WOOD,ModBlocks.BETTER_BARREL,ModBlocks.STONE_BETTER_BARREL
                    ,ModBlocks.COPPER_BETTER_BARREL,ModBlocks.IRON_BETTER_BARREL,ModBlocks.LAPIS_BETTER_BARREL,
                    ModBlocks.GOLD_BETTER_BARREL,ModBlocks.DIAMOND_BETTER_BARREL,ModBlocks.EMERALD_BETTER_BARREL,
                    ModBlocks.NETHERITE_BETTER_BARREL,ModBlocks.CREATIVE_BETTER_BARREL).build(null);
    public static class Suppliers {
        public static final BlockEntityType.BlockEntitySupplier<BetterBarrelBlockEntity> WOOD = BetterBarrelBlockEntity::new;
    }
}
