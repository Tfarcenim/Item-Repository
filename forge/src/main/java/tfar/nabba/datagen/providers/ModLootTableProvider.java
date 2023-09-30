package tfar.nabba.datagen.providers;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.*;
import net.minecraft.data.loot.packs.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import tfar.nabba.datagen.providers.loot.ModBlockLoot;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput packOutput, Set<ResourceLocation> resourceLocationSet) {
        super(packOutput,resourceLocationSet, create(packOutput).getTables());
    }

    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, BuiltInLootTables.all(), List.of(new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {

    }
}
