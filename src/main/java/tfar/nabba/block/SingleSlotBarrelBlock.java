package tfar.nabba.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.blockentity.SingleSlotBarrelBlockEntity;
import tfar.nabba.util.BarrelType;

import java.util.List;

public abstract class SingleSlotBarrelBlock extends AbstractBarrelBlock {

    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    public static final BooleanProperty INFINITE_VENDING = BooleanProperty.create("infinite_vending");

    public SingleSlotBarrelBlock(Properties pProperties, BarrelType type, BarrelFrameTier tier) {
        super(pProperties, type, tier);
        registerDefaultState(defaultBlockState().setValue(LOCKED, false).setValue(CONNECTED, true).setValue(INFINITE_VENDING,false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LOCKED, CONNECTED,INFINITE_VENDING);
    }

    @Override
    public void attack(BlockState pState, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
            ItemStack stack = betterBarrelBlockEntity.tryRemoveItem();
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        } else if (blockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
            FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(player.getMainHandItem(), fluidBarrelBlockEntity.getFluidHandler(),
                    new InvWrapper(player.getInventory()), Integer.MAX_VALUE, player, true);
            if (fluidActionResult.isSuccess()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, fluidActionResult.getResult());
            }
        }
    }

    //note,attack is not called if cancelling the left click block event
    @Override
    public void appendBlockStateInfo(CompoundTag tag, List<Component> tooltip) {
        if (!tag.isEmpty()) {
            super.appendBlockStateInfo(tag, tooltip);
            tooltip.add(Component.translatable("nabba.barrel.tooltip.locked").append(Component.literal(tag.getString(LOCKED.getName())).withStyle(ChatFormatting.YELLOW)));
            tooltip.add(Component.translatable("nabba.barrel.tooltip.connected").append(Component.literal(tag.getString(CONNECTED.getName())).withStyle(ChatFormatting.YELLOW)));
            tooltip.add(Component.translatable("nabba.barrel.tooltip.infinite_vending").append(Component.literal(tag.getString(INFINITE_VENDING.getName())).withStyle(ChatFormatting.YELLOW)));
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        //only check for controllers if this is a new block
        if ((blockEntity instanceof SingleSlotBarrelBlockEntity<?> singleSlotBarrelBlockEntity) && singleSlotBarrelBlockEntity.canConnect() && pOldState.getBlock() != pState.getBlock()) {
            singleSlotBarrelBlockEntity.searchForControllers();
        }
    }

    //caution, this method also gets called when changing blockstates
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean shouldRemove = pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity());

        if (shouldRemove) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SingleSlotBarrelBlockEntity<?> singleSlotBarrelBlockEntity) {
                singleSlotBarrelBlockEntity.removeController();
            }
        }

        if (!shouldRemove) {
            boolean connectChanged = pNewState.getValue(CONNECTED) != pState.getValue(CONNECTED);
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (connectChanged && blockEntity instanceof SingleSlotBarrelBlockEntity<?> singleSlotBarrelBlockEntity) {
                if (!pNewState.getValue(CONNECTED)) {
                    singleSlotBarrelBlockEntity.removeController();
                } else {
                    singleSlotBarrelBlockEntity.searchForControllers();
                }
            }
        }

        //they are the same block, check the states
        if (!shouldRemove) shouldRemove = pState.getValue(DISCRETE) != pNewState.getValue(DISCRETE);


        if (shouldRemove) {
            pLevel.removeBlockEntity(pPos);
        }
    }
}
