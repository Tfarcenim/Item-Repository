package tfar.nabba.block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.FluidMovingUtil;

import javax.annotation.Nullable;

public class FluidBarrelBlock extends SingleSlotBarrelBlock {


    public FluidBarrelBlock(Properties pProperties, BarrelFrameTier barrelTier) {
        super(pProperties, BarrelType.FLUID,barrelTier);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack handStack = pPlayer.getItemInHand(pHand);

        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof FluidBarrelBlockEntity betterBarrelBlockEntity) {
                Item item = handStack.getItem();
                //remember, this gets called before the item's onUse method
                if (item instanceof InteractsWithBarrel interactsWithBarrel && interactsWithBarrel.handleBarrel(pState,handStack,pLevel,pPos,pPlayer)) {

                } else {
                    Storage<FluidVariant> storage = betterBarrelBlockEntity.getFluidStorage();
                    FluidMovingUtil.interactWithFluidStorageFill(storage,pPlayer,InteractionHand.MAIN_HAND);
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(DISCRETE) ? ModBlockEntityTypes.Suppliers.DISCRETE_FB.create(pPos,pState):ModBlockEntityTypes.Suppliers.REGULAR_FB.create(pPos,pState);
    }
}
