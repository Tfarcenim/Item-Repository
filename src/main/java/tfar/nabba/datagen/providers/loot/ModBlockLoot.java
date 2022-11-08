package tfar.nabba.datagen.providers.loot;

import net.minecraft.data.loot.BlockLoot;
import tfar.nabba.init.ModBlocks;

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
}
