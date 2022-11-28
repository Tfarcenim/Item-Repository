package tfar.nabba.datagen.providers.assets;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.nabba.NABBA;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModBlocks;

import java.util.Locale;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, NABBA.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        makeAntiBarrel(ModBlocks.ANTI_BARREL);
        makeAntiBarrel(ModBlocks.STONE_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.COPPER_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.IRON_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.LAPIS_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.GOLD_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.DIAMOND_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.EMERALD_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.NETHERITE_ANTI_BARREL);
        makeAntiBarrel(ModBlocks.CREATIVE_ANTI_BARREL);

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

        makeBarrel(ModBlocks.FLUID_BARREL);
        makeBarrel(ModBlocks.COPPER_FLUID_BARREL);
        makeBarrel(ModBlocks.STONE_FLUID_BARREL);
        makeBarrel(ModBlocks.IRON_FLUID_BARREL);
        makeBarrel(ModBlocks.LAPIS_FLUID_BARREL);
        makeBarrel(ModBlocks.GOLD_FLUID_BARREL);
        makeBarrel(ModBlocks.DIAMOND_FLUID_BARREL);
        makeBarrel(ModBlocks.EMERALD_FLUID_BARREL);
        makeBarrel(ModBlocks.NETHERITE_FLUID_BARREL);
        makeBarrel(ModBlocks.CREATIVE_FLUID_BARREL);

        simpleBlock(ModBlocks.CONTROLLER);
    }

    protected void makeAntiBarrel(AbstractBarrelBlock block) {
        String name = Registry.BLOCK.getKey(block).getPath();
        String barrel = block.getBarrelTier().getName();

        BlockModelBuilder modelBuilder = models().withExistingParent(name,modLoc("block/"+block.getType().toString().toLowerCase(Locale.ROOT)+"_barrel_block"));
        modelBuilder
                .texture("frame_side",modLoc("block/"+barrel+"_barrel_frame_side"))
                .texture("frame_top",modLoc("block/"+barrel+"_barrel_frame_side"))
                .texture("frame_bottom",modLoc("block/"+barrel+"_barrel_frame_side"));

        getMultipartBuilder(block).part().modelFile(modelBuilder).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/barrel_void")))
                .addModel().condition(BetterBarrelBlock.VOID,true).end();
    }

    protected void makeBarrel(AbstractBarrelBlock block) {
        String name = Registry.BLOCK.getKey(block).getPath();
        String barrel = block.getBarrelTier().getName();

        BlockModelBuilder modelBuilder = models().withExistingParent(name,modLoc("block/"+block.getType().toString().toLowerCase(Locale.ROOT)+"_barrel_block"));
        modelBuilder
                .texture("frame_side",modLoc("block/"+barrel+"_barrel_frame_side"))
                .texture("frame_top",modLoc("block/"+barrel+"_barrel_frame_side"))
                .texture("frame_bottom",modLoc("block/"+barrel+"_barrel_frame_side"));

        getMultipartBuilder(block).part().modelFile(modelBuilder).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/barrel_void")))
                .addModel().condition(BetterBarrelBlock.VOID,true).end()
                .part().modelFile(models().getExistingFile(modLoc("block/barrel_lock")))
                .addModel().condition(BetterBarrelBlock.LOCKED,true).end()
                .part().modelFile(models().getExistingFile(modLoc("block/barrel_connected")))
                .addModel().condition(BetterBarrelBlock.CONNECTED,false).end();
    }
}
