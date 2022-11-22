package tfar.nabba.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.block.BetterBarrelBlock;

public class BarrelFrameUpgradeItem extends Item implements InteractsWithBarrel, InteractsWithController {
    private final BarrelFrameTier from;
    private final BarrelFrameTier to;

    public BarrelFrameUpgradeItem(Properties pProperties, BarrelFrameTier from, BarrelFrameTier to) {
        super(pProperties);
        this.from = from;
        this.to = to;
        //don't sidegrade or downgrade
        if (getDifference()<= 0) {
            throw new RuntimeException(to + " must be a higher tier than " + from);
        }
    }

    public int getDifference() {
        return to.getTier() - from.getTier();
    }

    public Pair<Integer,Integer> getUpgr() {
        return Pair.of(from.getTier(), to.getTier());
    }

    public BarrelFrameTier getTo() {
        return to;
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        AbstractBarrelBlock oldBarrel = (AbstractBarrelBlock) state.getBlock();
        if (!canUpgrade(oldBarrel) || itemstack.isEmpty()) return false;
        //saves the old barrels contents
        BlockState newState = to.getBarrel(oldBarrel.getType()).defaultBlockState();

        newState = copyBlockStates(state,newState);

        loadAndReplace(newState,level,pos);
        if (!player.getAbilities().instabuild) itemstack.shrink(1);
        return true;
    }

    public boolean canUpgrade(AbstractBarrelBlock block) {
        return block.getBarrelTier() == from;
    }

    public static BlockState copyBlockStates(BlockState old,BlockState newS) {
        if (newS.hasProperty(BetterBarrelBlock.DISCRETE)) {
            newS = newS.setValue(BetterBarrelBlock.DISCRETE,old.getValue(BetterBarrelBlock.DISCRETE));
        }

        if (newS.hasProperty(BetterBarrelBlock.LOCKED)) {
            newS = newS.setValue(BetterBarrelBlock.LOCKED,old.getValue(BetterBarrelBlock.LOCKED));
        }

        if (newS.hasProperty(BetterBarrelBlock.VOID)) {
            newS = newS.setValue(BetterBarrelBlock.VOID,old.getValue(BetterBarrelBlock.VOID));
        }
        return newS;
    }

    private static void loadAndReplace(BlockState newState, Level level, BlockPos pos) {
        BlockEntity oldBarrelEntity = level.getBlockEntity(pos);
        //saves the old barrels contents
        CompoundTag tag = oldBarrelEntity.saveWithoutMetadata();
        level.setBlockAndUpdate(pos, newState);
        //get the blockentity that now exists
        BlockEntity newBlockEntity = level.getBlockEntity(pos);
        newBlockEntity.load(tag);
        //need to make sure the game saves it!
        newBlockEntity.setChanged();
    }

    @Override
    public boolean handleController(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        return interactWithBarrels(state, itemstack, level, pos, player);
    }
}
