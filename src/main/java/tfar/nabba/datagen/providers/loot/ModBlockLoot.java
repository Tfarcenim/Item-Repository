package tfar.nabba.datagen.providers.loot;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import tfar.nabba.NABBA;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.util.NBTKeys;

import java.util.stream.Collectors;

public class ModBlockLoot extends BlockLoot {

    @Override
    protected void addTables() {
        dropBetterBarrel(ModBlocks.BETTER_BARREL);
        dropBetterBarrel(ModBlocks.STONE_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.COPPER_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.IRON_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.LAPIS_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.GOLD_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.DIAMOND_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.EMERALD_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.NETHERITE_BETTER_BARREL);
        dropBetterBarrel(ModBlocks.CREATIVE_BETTER_BARREL);

        dropAntiBarrel(ModBlocks.ANTI_BARREL);
        dropAntiBarrel(ModBlocks.STONE_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.COPPER_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.IRON_ANTI_BARREL);

        dropSelf(ModBlocks.CONTROLLER);
    }

    protected void dropBetterBarrel(Block block) {

        LootTable.Builder builder = LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyBlockState.copyState(block).copy(BetterBarrelBlock.DISCRETE).copy(BetterBarrelBlock.LOCKED).copy(BetterBarrelBlock.VOID))
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy(NBTKeys.Color.name(),"BlockEntityTag."+NBTKeys.Color)
                                        .copy(NBTKeys.Stack.name(),"BlockEntityTag."+NBTKeys.Stack)
                                        .copy(NBTKeys.RealCount.name(),"BlockEntityTag."+NBTKeys.RealCount)
                                        .copy(NBTKeys.Upgrades.name(),"BlockEntityTag."+NBTKeys.Upgrades)
                                        .copy(NBTKeys.Size.name(),"BlockEntityTag."+NBTKeys.Size)
                                        .copy(NBTKeys.Ghost.name(),"BlockEntityTag."+NBTKeys.Ghost)
                                )
                        )
                        )
                );
        this.add(block,builder);
    }

    protected void dropAntiBarrel(Block block) {
        LootTable.Builder builder = LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block)
                                        .apply(CopyBlockState.copyState(block).copy(BetterBarrelBlock.VOID))
                                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                .copy(NBTKeys.Settings.name(),"BlockEntityTag."+NBTKeys.Settings)
                                        )
                                )
                        )
                );
        this.add(block,builder);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registry.BLOCK.stream().filter(block -> (Registry.BLOCK.getKey(block).getNamespace().equals(NABBA.MODID))).collect(Collectors.toList());
    }
}
