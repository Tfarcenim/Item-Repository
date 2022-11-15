package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.item.InteractsWithBarrel;
import tfar.nabba.item.KeyItem;
import tfar.nabba.item.VanityKeyItem;
import tfar.nabba.util.Utils;

import java.util.List;

public class ControllerBlock extends Block implements EntityBlock {
    public ControllerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ControllerBlockEntity controllerBlock) {
                //special case the vanity key
                if (stack.getItem() instanceof VanityKeyItem) {
                    ((VanityKeyItem) stack.getItem()).handleBarrel(pState,stack,pLevel,pPos,pPlayer);
                } else if (stack.getItem() instanceof KeyItem keyItem) {
                    controllerBlock.interactWithBarrels(stack,pPlayer);
                }

            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.literal("Connects to all barrels and tanks from this mod within a "
                +(2 * Utils.RADIUS+1)+"x"+(2 * Utils.RADIUS+1)+"x"+(2 * Utils.RADIUS+1)+" volume"));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ControllerBlockEntity.create(pPos, pState);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof ControllerBlockEntity controllerBlock) {
            controllerBlock.gatherBarrels();
        }
    }
}
