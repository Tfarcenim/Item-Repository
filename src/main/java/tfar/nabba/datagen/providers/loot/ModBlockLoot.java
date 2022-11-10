package tfar.nabba.datagen.providers.loot;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import tfar.nabba.NABBA;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlocks;

import java.util.stream.Collectors;

public class ModBlockLoot extends BlockLoot {

    @Override
    protected void addTables() {
        dropBarrel(ModBlocks.BETTER_BARREL);
        dropBarrel(ModBlocks.STONE_BETTER_BARREL);
        dropBarrel(ModBlocks.COPPER_BETTER_BARREL);
        dropBarrel(ModBlocks.IRON_BETTER_BARREL);
        dropBarrel(ModBlocks.LAPIS_BETTER_BARREL);
        dropBarrel(ModBlocks.GOLD_BETTER_BARREL);
        dropBarrel(ModBlocks.DIAMOND_BETTER_BARREL);
        dropBarrel(ModBlocks.EMERALD_BETTER_BARREL);
        dropBarrel(ModBlocks.NETHERITE_BETTER_BARREL);

        dropBarrel(ModBlocks.CREATIVE_BETTER_BARREL);

        dropSelf(ModBlocks.ANTI_BARREL);
    }

    protected void dropBarrel(Block block) {

        LootTable.Builder builder = LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).apply(CopyBlockState.copyState(block)
                                .copy(BetterBarrelBlock.DISCRETE).copy(BetterBarrelBlock.LOCKED).copy(BetterBarrelBlock.VOID)))

                ));


        this.add(block,builder);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registry.BLOCK.stream().filter(block -> (Registry.BLOCK.getKey(block).getNamespace().equals(NABBA.MODID))).collect(Collectors.toList());
    }
}
