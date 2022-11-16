package tfar.nabba.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.api.HasItemHandler;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.init.tag.ModBlockEntityTypeTags;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static tfar.nabba.block.BetterBarrelBlock.VOID;

public class Utils {
    public static final int INVALID = -1;
    public static String ID = "id";
    public static final Map<BarrelType,Integer> BASE_STORAGE = new EnumMap<>(BarrelType.class);

    static {
        BASE_STORAGE.put(BarrelType.BETTER,64);//stacks
        BASE_STORAGE.put(BarrelType.ANTI,256);//unstackables
        BASE_STORAGE.put(BarrelType.FLUID,16);//buckets
    }
    public static final int RADIUS = 9;

    public static final double SIZE = .5;
    public static final int COLOR = 0xff88ff;

    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> add_to_internal_upgrades = (betterBarrelBlockEntity, upgradeData) -> {

        int existing = betterBarrelBlockEntity.countUpgrade(upgradeData.getData());
        if (existing == 0) {
            betterBarrelBlockEntity.getUpgrades().add(upgradeData.copy());//do not use the original
        } else {
            for (UpgradeStack upgradeStack : betterBarrelBlockEntity.getUpgrades()) {
                if (upgradeStack.getData() == upgradeData.getData()) {
                    upgradeStack.grow(upgradeData.getCount());break;
                }
            }
        }
    };

    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> apply_void = (betterBarrelBlockEntity, upgradeData) -> {
        BlockState state = betterBarrelBlockEntity.getBlockState();
        betterBarrelBlockEntity.getLevel().setBlock(betterBarrelBlockEntity.getBlockPos(),state.setValue(VOID,true),3);
    };

    public static final BiConsumer<AbstractBarrelBlockEntity, UpgradeStack> PICKUP_TICK =
            (betterBarrelBlockEntity,upgradeDataStack) -> pickupInABox(betterBarrelBlockEntity, upgradeDataStack.getCount(), 3, upgradeDataStack.getCount());

    public static void pickupInABox(AbstractBarrelBlockEntity betterBarrelBlockEntity, int x, int y, int z) {
        if (betterBarrelBlockEntity instanceof HasItemHandler) {

            Level level = betterBarrelBlockEntity.getLevel();
            //note, AABBs start at 0,0,0 on the blockEntity, so to get a 3x3x3 cube we need to go from -1,-1,-1 to +2,+2,+2 relative
            List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class,
                    getBoxCenteredOn(betterBarrelBlockEntity.getBlockPos(), x, y, z)
            );
            for (ItemEntity itemEntity : itemEntities) {
                addItem((HasItemHandler) betterBarrelBlockEntity, itemEntity);
            }
        }
    }

    public static AABB getBoxCenteredOn(BlockPos pos, int side) {
        return getBoxCenteredOn(pos,side,side,side);
    }
    public static AABB getBoxCenteredOn(BlockPos pos, int x,int y,int z) {
        return new AABB(
                pos.offset((1-z)/2d,(1-y)/2d,(1-z)/2d),
                pos.offset((x+1)/2d,(y+1)/2d,(z+1)/2d)
        );
    }


    public static boolean addItem(HasItemHandler betterBarrelBlockEntity, ItemEntity pItem) {
        boolean flag = false;
        ItemStack itemstack = pItem.getItem().copy();
        ItemStack itemstack1 = betterBarrelBlockEntity.tryAddItem(itemstack);
        if (itemstack1.isEmpty()) {
            flag = true;
            pItem.discard();
        } else {
            pItem.setItem(itemstack1);
        }
        return flag;
    }

    public static List<BlockEntity> getNearbyBarrels(Level level, BlockPos thisPos) {
        return getNearbyBlockEntities(level, isBetterBarrel,thisPos);
    }

    public static List<BlockEntity> getNearbyControllers(Level level, BlockPos thisPos) {
        return getNearbyBlockEntities(level,isController,thisPos);
    }

    public static final Predicate<BlockEntity> isController = ControllerBlockEntity.class::isInstance;
    public static final Predicate<BlockEntity> isBetterBarrel = blockEntity -> ForgeRegistries.BLOCK_ENTITY_TYPES.tags().getTag(ModBlockEntityTypeTags.BETTER_BARRELS).contains(blockEntity.getType());
    //searches a 3x3 chunk area
    public static List<BlockEntity> getNearbyBlockEntities(Level level, Predicate<BlockEntity> predicate, BlockPos thisPos) {

        int chunkX = SectionPos.blockToSectionCoord(thisPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(thisPos.getZ());
        List<BlockEntity> blockentities = new ArrayList<>();
        for (int z = -1; z <= 1;z++) {
            for (int x = -1; x <= 1;x++) {
                LevelChunk chunk = level.getChunk(chunkX + x,chunkZ + z);
                Map<BlockPos,BlockEntity> blockEntities = chunk.getBlockEntities();
                for (Map.Entry<BlockPos,BlockEntity> entry: blockEntities.entrySet()) {
                    BlockEntity blockEntity = entry.getValue();
                    if (predicate.test(blockEntity)) {
                        BlockPos pos = entry.getKey();
                        if (Math.abs(pos.getX() - thisPos.getX() ) < Utils.RADIUS
                                && Math.abs(pos.getY() - thisPos.getY() ) < Utils.RADIUS
                                && Math.abs(pos.getZ() - thisPos.getZ() ) < Utils.RADIUS) {
                            blockentities.add(entry.getValue());
                        }
                    }
                }
            }
        }
        return blockentities;
    }

    @NotNull
    public static FluidStack copyFluidWithSize(@NotNull FluidStack itemStack, int size) {
        if (size == 0) return FluidStack.EMPTY;
        return new FluidStack(itemStack,size);
    }
}
