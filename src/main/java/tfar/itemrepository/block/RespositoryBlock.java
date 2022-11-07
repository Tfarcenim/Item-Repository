package tfar.itemrepository.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tfar.itemrepository.blockentity.RepositoryBlockEntity;

public class RespositoryBlock extends Block implements EntityBlock {
    public RespositoryBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult result) {
        if (!world.isClientSide) {
            MenuProvider tileEntity = state.getMenuProvider(world, pos);
            if (tileEntity != null) {
                player.openMenu(tileEntity);
                PiglinAi.angerNearbyPiglins(player, true);
            }
            return InteractionResult.CONSUME;

        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RepositoryBlockEntity(pPos, pState);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof RepositoryBlockEntity repositoryBlockEntity) {
            if (pStack.hasCustomHoverName()) {
                repositoryBlockEntity.setCustomName(pStack.getHoverName());
            }
            repositoryBlockEntity.initialize(pStack);
        }
    }
}
