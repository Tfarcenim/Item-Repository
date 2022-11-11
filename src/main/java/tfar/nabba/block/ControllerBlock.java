package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tfar.nabba.item.KeyItem;

public class ControllerBlock extends Block {
    public ControllerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.getItem() instanceof KeyItem keyItem) {
            for (int z = -15; z < 15;z++) {
                for (int y = -15; y < 15;y++) {
                    for (int x = -15; x < 15; x++) {
                        BlockPos pos = pPos.offset(x,y,z);
                        keyItem.handleBarrel(pLevel.getBlockState(pos), stack, pLevel,pos, pPlayer);
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
}
