package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BarrelInterfaceBlockEntity(pPos,pState);
    }
}
