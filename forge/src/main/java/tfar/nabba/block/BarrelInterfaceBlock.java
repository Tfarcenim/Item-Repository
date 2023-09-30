package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import tfar.nabba.NABBAForge;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;

import java.util.List;

public class BarrelInterfaceBlock extends Block implements EntityBlock {
    public BarrelInterfaceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable(getDescriptionId() +".tooltip", NABBAForge.ServerCfg.barrel_interface_storage.get()));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            Item item = stack.getItem();
            if (!(item instanceof InteractsWithController interactsWithBarrel) || !interactsWithBarrel.handleController(state, stack, world, pos, player)) {
                MenuProvider menuProvider = state.getMenuProvider(world, pos);
                if (menuProvider != null) {
                    player.openMenu(menuProvider);
                    PiglinAi.angerNearbyPiglins(player, true);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return  InteractionResult.SUCCESS;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof BarrelInterfaceBlockEntity barrelInterfaceBlockEntity) {
                dropContents(pLevel, pPos, barrelInterfaceBlockEntity.getInventory().getBarrels());
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public static void dropContents(Level pLevel, BlockPos pPos, List<ItemStack> pStackList) {
        pStackList.forEach(stack -> Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), stack));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BarrelInterfaceBlockEntity(pPos,pState);
    }
}
