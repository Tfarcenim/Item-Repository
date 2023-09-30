package tfar.nabba.datagen.providers.loot;

import tfar.nabba.NABBA;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.util.NBTKeys;

import java.util.Set;

public class ModBlockLoot extends BlockLootSubProvider {

    public ModBlockLoot() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
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

        dropBarrel(ModBlocks.FLUID_BARREL);
        dropBarrel(ModBlocks.STONE_FLUID_BARREL);
        dropBarrel(ModBlocks.COPPER_FLUID_BARREL);
        dropBarrel(ModBlocks.IRON_FLUID_BARREL);
        dropBarrel(ModBlocks.LAPIS_FLUID_BARREL);
        dropBarrel(ModBlocks.GOLD_FLUID_BARREL);
        dropBarrel(ModBlocks.DIAMOND_FLUID_BARREL);
        dropBarrel(ModBlocks.EMERALD_FLUID_BARREL);
        dropBarrel(ModBlocks.NETHERITE_FLUID_BARREL);
        dropBarrel(ModBlocks.CREATIVE_FLUID_BARREL);

        dropAntiBarrel(ModBlocks.ANTI_BARREL);
        dropAntiBarrel(ModBlocks.STONE_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.COPPER_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.IRON_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.LAPIS_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.GOLD_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.DIAMOND_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.EMERALD_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.NETHERITE_ANTI_BARREL);
        dropAntiBarrel(ModBlocks.CREATIVE_ANTI_BARREL);

        dropSelf(ModBlocks.CONTROLLER);
        dropSelf(ModBlocks.BARREL_INTERFACE);
        dropSelf(ModBlocks.CONTROLLER_PROXY);
    }

    protected void dropBarrel(Block block) {

        LootTable.Builder builder = LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block)
                                        .apply(copySharedBlockStates(block).copy(BetterBarrelBlock.LOCKED).copy(BetterBarrelBlock.CONNECTED).copy(BetterBarrelBlock.INFINITE_VENDING).copy(SingleSlotBarrelBlock.STORAGE_DOWNGRADE))
                                        .apply(copySharedNBTInfo()
                                                .copy(NBTKeys.Stack.name(), "BlockEntityTag." + NBTKeys.Stack)
                                                .copy(NBTKeys.RealCount.name(), "BlockEntityTag." + NBTKeys.RealCount)
                                                .copy(NBTKeys.Ghost.name(), "BlockEntityTag." + NBTKeys.Ghost)
                                        )
                                )
                        )
                );
        this.add(block, builder);
    }

    protected void dropAntiBarrel(Block block) {
        LootTable.Builder builder = LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block)
                                        .apply(copySharedBlockStates(block))
                                        .apply(copySharedNBTInfo()
                                                .copy(NBTKeys.Uuid.name(), NBTKeys.Uuid.name())
                                                .copy("Last", "BlockEntityTag.Last")
                                                .copy("Stored", "BlockEntityTag.Stored")
                                        )
                                )
                        )
                );
        this.add(block, builder);
    }

    public CopyNbtFunction.Builder copySharedNBTInfo() {
        return CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                .copy(NBTKeys.Color.name(), "BlockEntityTag." + NBTKeys.Color)
                .copy(NBTKeys.Upgrades.name(), "BlockEntityTag." + NBTKeys.Upgrades)
                .copy(NBTKeys.Size.name(), "BlockEntityTag." + NBTKeys.Size);
    }

    public CopyBlockState.Builder copySharedBlockStates(Block block) {
        return CopyBlockState.copyState(block).copy(BetterBarrelBlock.DISCRETE).copy(BetterBarrelBlock.VOID).copy(AbstractBarrelBlock.REDSTONE);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(NABBA.MODID))
                .toList();
    }
}
