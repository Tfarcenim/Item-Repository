package tfar.itemrepository.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.itemrepository.RepositoryBlockEntity;

public class ModBlockEntityTypes {
    public static final BlockEntityType<RepositoryBlockEntity> REPOSITORY = BlockEntityType.Builder.of(RepositoryBlockEntity::new,ModBlocks.REPOSITORY).build(null);
}
