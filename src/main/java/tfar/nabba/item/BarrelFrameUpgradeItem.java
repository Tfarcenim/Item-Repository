package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

public class BarrelFrameUpgradeItem extends Item implements InteractsWithBarrel {
    private final BarrelFrameTier from;
    private final BarrelFrameTier to;

    public BarrelFrameUpgradeItem(Properties pProperties, BarrelFrameTier from, BarrelFrameTier to) {
        super(pProperties);
        //don't sidegrade or downgrade
        if (from.getTier() >= to.getTier()) {
            throw new RuntimeException(to + " must be a higher tier than " + from);
        }
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        AbstractBarrelBlock oldBarrel = (AbstractBarrelBlock) state.getBlock();
        if (oldBarrel.getBarrelTier() != from) return false;
        //saves the old barrels contents
        BlockState newState = to.getBarrel(oldBarrel.getType()).defaultBlockState();
        loadAndReplace(state,newState,level,pos);
        if (!player.getAbilities().instabuild) itemstack.shrink(1);
        return true;
    }


    public static void loadAndReplace(BlockState oldState, BlockState newState, Level level, BlockPos pos) {
        BlockEntity oldBarrelEntity = level.getBlockEntity(pos);
        //saves the old barrels contents
        CompoundTag tag = oldBarrelEntity.saveWithoutMetadata();

        if (newState.hasProperty(BetterBarrelBlock.LOCKED)) {
            newState = newState.setValue(BetterBarrelBlock.LOCKED, oldState.getValue(BetterBarrelBlock.LOCKED));
        }

        if (newState.hasProperty(BetterBarrelBlock.VOID)) {
            newState = newState.setValue(BetterBarrelBlock.VOID, oldState.getValue(BetterBarrelBlock.VOID));
        }

        level.setBlock(pos, newState, 3);
        //get the blockentity that now exists
        BlockEntity newBlockEntity = level.getBlockEntity(pos);
        newBlockEntity.load(tag);
        //need to make sure the game saves it!
        newBlockEntity.setChanged();
    }
}
