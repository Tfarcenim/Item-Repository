package tfar.nabba.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

import java.util.List;
import java.util.function.BiConsumer;

import static tfar.nabba.block.BetterBarrelBlock.VOID;

public class Utils {
    public static final int INVALID = -1;
    public static String ID = "id";
    public static final int BASE_STORAGE = 64;
    public static final int RADIUS = 9;

    public static final BiConsumer<BetterBarrelBlockEntity, UpgradeStack> add_to_internal_upgrades = (betterBarrelBlockEntity, upgradeData) -> {

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

    public static final BiConsumer<BetterBarrelBlockEntity, UpgradeStack> apply_void = (betterBarrelBlockEntity, upgradeData) -> {
        BlockState state = betterBarrelBlockEntity.getBlockState();
        betterBarrelBlockEntity.getLevel().setBlock(betterBarrelBlockEntity.getBlockPos(),state.setValue(VOID,true),3);
    };

    public static final BiConsumer<BetterBarrelBlockEntity, UpgradeStack> PICKUP_TICK =
            (betterBarrelBlockEntity,upgradeDataStack) -> pickupItemsInBox(betterBarrelBlockEntity, upgradeDataStack.getCount(), 3, upgradeDataStack.getCount());

    public static void pickupItemsInBox(BetterBarrelBlockEntity betterBarrelBlockEntity,int x,int y, int z) {
        Level level = betterBarrelBlockEntity.getLevel();
        //note, AABBs start at 0,0,0 on the blockEntity, so to get a 3x3x3 cube we need to go from -1,-1,-1 to +2,+2,+2 relative
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class,
                getBoxCenteredOn(betterBarrelBlockEntity.getBlockPos(),x,y,z)
        );
        for (ItemEntity itemEntity : itemEntities) {
            addItem(betterBarrelBlockEntity,itemEntity);
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


    public static boolean addItem(BetterBarrelBlockEntity betterBarrelBlockEntity, ItemEntity pItem) {
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
}
