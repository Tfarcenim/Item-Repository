package tfar.itemrepository.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.itemrepository.ItemRepository;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ItemRepository.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
