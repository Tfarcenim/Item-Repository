package tfar.nabba.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.blockentity.SingleSlotBarrelBlockEntity;
import tfar.nabba.util.BarrelType;

import java.util.List;

public abstract class SingleSlotBarrelBlock extends AbstractBarrelBlock {
    public SingleSlotBarrelBlock(Properties pProperties, BarrelType type, BarrelFrameTier tier) {
        super(pProperties, type, tier);
        registerDefaultState(defaultBlockState().setValue(LOCKED,false).setValue(CONNECTED,true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LOCKED,CONNECTED);
    }

    //note,attack is not called if cancelling the left click block event
    @Override
    public void appendBlockStateInfo(CompoundTag tag, List<Component> tooltip) {
        if (!tag.isEmpty()) {
            super.appendBlockStateInfo(tag, tooltip);
            tooltip.add(Component.literal("Locked: ").append(Component.literal(tag.getString(LOCKED.getName())).withStyle(ChatFormatting.YELLOW)));
            tooltip.add(Component.literal("Connected: ").append(Component.literal(tag.getString(CONNECTED.getName())).withStyle(ChatFormatting.YELLOW)));
        }
    }

    //caution, this method also gets called when changing blockstates
    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        //only check for controllers if this is a new block
        if ((blockEntity instanceof SingleSlotBarrelBlockEntity<?> singleSlotBarrelBlockEntity)  && pOldState.getBlock() != pState.getBlock()) {
            singleSlotBarrelBlockEntity.searchForControllers();
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean shouldRemove = pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity());

        if (shouldRemove) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SingleSlotBarrelBlockEntity<?> singleSlotBarrelBlockEntity) {
                singleSlotBarrelBlockEntity.removeController();
            }
        }

        //they are the same block, check the states
        if (!shouldRemove) shouldRemove = pState.getValue(DISCRETE) != pNewState.getValue(DISCRETE);

        if (shouldRemove) {
            pLevel.removeBlockEntity(pPos);
        }
    }
}
