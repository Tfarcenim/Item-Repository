package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.block.BetterBarrelBlock;

public class HideKeyItem extends KeyItem {
    public HideKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer) {
        BlockState newState = state.setValue(BetterBarrelBlock.DISCRETE,!state.getValue(BetterBarrelBlock.DISCRETE));
        level.setBlock(pos,newState,3);
        level.sendBlockUpdated(pos,state,newState,3);
        return true;
    }
}
