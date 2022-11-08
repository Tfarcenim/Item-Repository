package tfar.itemrepository.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.itemrepository.datagen.providers.ModBlockStateProvider;
import tfar.itemrepository.datagen.providers.ModItemModelProvider;
import tfar.itemrepository.datagen.providers.ModLangProvider;
import tfar.itemrepository.datagen.providers.ModRecipeProvider;

public class ModDatagen {
    public static void start(GatherDataEvent e) {
        DataGenerator dataGenerator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        boolean client = e.includeClient();
        boolean server = e.includeServer();
        dataGenerator.addProvider(client,new ModBlockStateProvider(dataGenerator,helper));
        dataGenerator.addProvider(client,new ModItemModelProvider(dataGenerator,helper));
        dataGenerator.addProvider(client,new ModLangProvider(dataGenerator));

        dataGenerator.addProvider(server,new ModRecipeProvider(dataGenerator));
    }
}
