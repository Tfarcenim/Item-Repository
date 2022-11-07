package tfar.itemrepository.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.itemrepository.ItemRepository;
import tfar.itemrepository.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ItemRepository.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.BETTER_BARREL,models()
                .cubeBottomTop("better_barrel",modLoc( "block/better_barrel_side"),modLoc( "block/better_barrel_bottom"),modLoc( "block/better_barrel_top")));
    }
}
