package tfar.itemrepository.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import tfar.itemrepository.RespositoryBlock;

public class ModBlocks {
    public static final Block REPOSITORY = new RespositoryBlock(BlockBehaviour.Properties.of(Material.METAL));
}
