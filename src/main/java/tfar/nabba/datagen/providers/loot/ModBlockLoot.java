package tfar.nabba.datagen.providers.loot;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;

import java.util.stream.Collectors;

public class ModBlockLoot extends BlockLoot {

    @Override
    protected void addTables() {
        dropSelf(ModBlocks.BETTER_BARREL);
        dropSelf(ModBlocks.STONE_BETTER_BARREL);
        dropSelf(ModBlocks.COPPER_BETTER_BARREL);
        dropSelf(ModBlocks.IRON_BETTER_BARREL);
        dropSelf(ModBlocks.LAPIS_BETTER_BARREL);
        dropSelf(ModBlocks.GOLD_BETTER_BARREL);
        dropSelf(ModBlocks.DIAMOND_BETTER_BARREL);
        dropSelf(ModBlocks.EMERALD_BETTER_BARREL);
        dropSelf(ModBlocks.NETHERITE_BETTER_BARREL);
        dropSelf(ModBlocks.CREATIVE_BETTER_BARREL);

        dropSelf(ModBlocks.ANTI_BARREL);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registry.BLOCK.stream().filter(block -> (Registry.BLOCK.getKey(block).getNamespace().equals(NABBA.MODID))).collect(Collectors.toList());
    }
}
