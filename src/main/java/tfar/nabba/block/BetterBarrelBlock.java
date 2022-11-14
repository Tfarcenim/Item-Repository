package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.item.InteractsWithBarrel;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.util.BarrelType;

import javax.annotation.Nullable;
import java.util.List;

public class BetterBarrelBlock extends AbstractBarrelBlock {


    public BetterBarrelBlock(Properties pProperties, BarrelFrameTier barrelTier) {
        super(pProperties, BarrelType.BETTER,barrelTier);
        registerDefaultState(defaultBlockState().setValue(LOCKED,false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack handStack = pPlayer.getItemInHand(pHand);

        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
                Item item = handStack.getItem();

                //remember, this gets called before the item's onUse method
                if (item instanceof InteractsWithBarrel interactsWithBarrel && interactsWithBarrel.handleBarrel(pState,handStack,pLevel,pPos,pPlayer)) {

                } else {

                    ItemStack existing = betterBarrelBlockEntity.getBarrelHandler().getStack();
                    //there is no items in the barrel OR the item that the player is holding is the same as the item in the barrel
                    if (existing.isEmpty() || ItemStack.isSameItemSameTags(handStack,existing)) {
                        ItemStack stack = betterBarrelBlockEntity.tryAddItem(handStack);
                        pPlayer.setItemInHand(pHand, stack);
                    } else {
                        //search the entire inventory for item stacks
                        Inventory inventory = pPlayer.getInventory();
                        NonNullList<ItemStack> main = inventory.items;
                        for (int i = 0; i < main.size();i++) {
                            ItemStack fromPlayer = main.get(i);
                            if (!fromPlayer.isEmpty()) {
                                ItemStack insert = betterBarrelBlockEntity.tryAddItem(fromPlayer);
                                //if the item changed, something happened
                                if (insert != fromPlayer) {
                                    main.set(i, insert);
                                }
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    //note,attack is not called if cancelling the left click block event

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(DISCRETE) ? ModBlockEntityTypes.Suppliers.DISCRETE.create(pPos,pState):ModBlockEntityTypes.Suppliers.REGULAR.create(pPos,pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean shouldRemove = pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity());

        if (shouldRemove) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlock) {
                betterBarrelBlock.removeController();
            }
        }

        //they are the same block, check the states
        if (!shouldRemove) shouldRemove = pState.getValue(DISCRETE) != pNewState.getValue(DISCRETE);

        if (shouldRemove) {
            pLevel.removeBlockEntity(pPos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand();

        return super.getStateForPlacement(pContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LOCKED);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (Level pLevel1, BlockPos pPos, BlockState pState1, T pBlockEntity) -> BetterBarrelBlockEntity.serverTick(pLevel1, pPos, pState1, (BetterBarrelBlockEntity) pBlockEntity);
    }


}
