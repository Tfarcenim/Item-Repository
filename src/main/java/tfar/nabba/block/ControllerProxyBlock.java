package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.blockentity.ControllerProxyBlockEntity;

import java.util.List;

public class ControllerProxyBlock extends Block implements EntityBlock {
    public ControllerProxyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ControllerProxyBlockEntity controllerProxy) {
                ControllerBlockEntity controllerBlock = controllerProxy.getController();
                if (controllerBlock != null) {
                   return controllerBlock.getBlockState().getBlock().use(controllerBlock.getBlockState(),pLevel,controllerBlock.getBlockPos(),pPlayer,pHand,pHit);
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable(getDescriptionId() +".tooltip"));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ControllerProxyBlockEntity.create(pPos, pState);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof ControllerProxyBlockEntity controllerBlock) {
            controllerBlock.searchForControllers();
        }
    }
}
