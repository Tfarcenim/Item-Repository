package tfar.nabba.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.nabba.datagen.providers.*;
import tfar.nabba.datagen.providers.assets.ModBlockStateProvider;
import tfar.nabba.datagen.providers.assets.ModItemModelProvider;
import tfar.nabba.datagen.providers.assets.ModLangProvider;
import tfar.nabba.datagen.providers.data.tags.ModBlockEntityTagsProvider;
import tfar.nabba.datagen.providers.data.tags.ModBlockTagsProvider;
import tfar.nabba.datagen.providers.data.tags.ModItemTagsProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatagen {
    public static void start(GatherDataEvent e) {
        DataGenerator dataGenerator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();

        boolean client = e.includeClient();
        boolean server = e.includeServer();
        dataGenerator.addProvider(client,new ModBlockStateProvider(dataGenerator,helper));
        dataGenerator.addProvider(client,new ModItemModelProvider(dataGenerator,helper));
        dataGenerator.addProvider(client,new ModLangProvider(dataGenerator));

        dataGenerator.addProvider(server,new ModRecipeProvider(packOutput));
        dataGenerator.addProvider(server,new ModLootTableProvider(packOutput, Set.of()));
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider,helper);
        dataGenerator.addProvider(server,blockTagsProvider);
        dataGenerator.addProvider(server,new ModItemTagsProvider(packOutput,lookupProvider,blockTagsProvider,helper));
        dataGenerator.addProvider(server,new ModBlockEntityTagsProvider(packOutput,lookupProvider,helper));
    }
}
