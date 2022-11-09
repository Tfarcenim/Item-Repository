package tfar.nabba.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.nabba.datagen.providers.*;
import tfar.nabba.datagen.providers.assets.ModBlockStateProvider;
import tfar.nabba.datagen.providers.assets.ModItemModelProvider;
import tfar.nabba.datagen.providers.assets.ModLangProvider;

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
        dataGenerator.addProvider(server,new ModLootTableProvider(dataGenerator));
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(dataGenerator,helper);
        dataGenerator.addProvider(server,blockTagsProvider);
        dataGenerator.addProvider(server,new ModItemTagsProvider(dataGenerator,blockTagsProvider,helper));
    }
}
