package tfar.nabba.datagen.providers.assets;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.nabba.NABBA;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, NABBA.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        makeBarrel(ModBlocks.BETTER_BARREL);
        makeBarrel(ModBlocks.COPPER_BETTER_BARREL);
        makeBarrel(ModBlocks.STONE_BETTER_BARREL);
        makeBarrel(ModBlocks.IRON_BETTER_BARREL);
        makeBarrel(ModBlocks.LAPIS_BETTER_BARREL);
        makeBarrel(ModBlocks.GOLD_BETTER_BARREL);
        makeBarrel(ModBlocks.DIAMOND_BETTER_BARREL);
        makeBarrel(ModBlocks.EMERALD_BETTER_BARREL);
        makeBarrel(ModBlocks.NETHERITE_BETTER_BARREL);
        makeBarrel(ModBlocks.CREATIVE_BETTER_BARREL);
    }

    protected void makeBarrel(Block block) {
        String name = Registry.BLOCK.getKey(block).getPath();

        BlockModelBuilder modelBuilder = models().withExistingParent(name,modLoc("block/better_barrel_block"));
        modelBuilder
                .texture("frame_side",modLoc("block/"+name+"_frame_side"))
                .texture("frame_top",modLoc("block/"+name+"_frame_side"))
                .texture("frame_bottom",modLoc("block/"+name+"_frame_side"));

        getMultipartBuilder(block).part().modelFile(modelBuilder).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/better_barrel_void"))).addModel().condition(BetterBarrelBlock.VOID,true).end();
    }
}
