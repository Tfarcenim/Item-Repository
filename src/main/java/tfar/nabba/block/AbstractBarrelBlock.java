package tfar.nabba.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import tfar.nabba.NABBA;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.item.barrels.BetterBarrelBlockItem;
import tfar.nabba.util.BarrelType;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractBarrelBlock extends Block implements EntityBlock {

    public static final String info = NABBA.MODID+".barrel.info";
    protected final BarrelFrameTier barrelTier;
    public static final BooleanProperty VOID = BooleanProperty.create("void");
    public static final BooleanProperty DISCRETE = BooleanProperty.create("discrete");
    public static final BooleanProperty REDSTONE = BooleanProperty.create("redstone");
    public static final DirectionProperty H_FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final BarrelType type;

    public AbstractBarrelBlock(Properties pProperties, BarrelType type,BarrelFrameTier tier) {
        super(pProperties);
        this.type = type;
        this.barrelTier = tier;
        registerDefaultState(this.stateDefinition.any().setValue(VOID,false).setValue(DISCRETE,false).setValue(REDSTONE,false));
    }

    public BarrelFrameTier getBarrelTier() {
        return barrelTier;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        if (pStack.hasTag()) {
            appendBlockStateInfo(pStack.getTag().getCompound("BlockStateTag"), pTooltip);

            List<UpgradeStack> upgradeStacks = BetterBarrelBlockItem.getUpgrades(pStack);

            if (!upgradeStacks.isEmpty()) {
                pTooltip.add(Component.literal("nabba.barrel.tooltip"));
                for (UpgradeStack upgradeStack: upgradeStacks) {
                    MutableComponent mutablecomponent = upgradeStack.getName();
                    pTooltip.add(mutablecomponent);
                }
            }
       //     pTooltip.add(Component.literal(pStack.getTag().toString()).withStyle(ChatFormatting.GOLD));
        }
        pTooltip.add(Component.translatable(info,
                Component.translatable(BetterBarrelBlockItem.getUsedSlotsFromItem(pStack)+"/"+barrelTier.getUpgradeSlots()).withStyle(ChatFormatting.AQUA)));
    }

    public void appendBlockStateInfo(CompoundTag tag, List<Component> tooltip) {
        if (!tag.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("nabba.barrel.tooltip.discrete").append(Component.literal(tag.getString(DISCRETE.getName())).withStyle(ChatFormatting.YELLOW)));
            tooltip.add(Component.translatable("nabba.barrel.tooltip.void").append(Component.literal(tag.getString(VOID.getName())).withStyle(ChatFormatting.YELLOW)));
            tooltip.add(Component.translatable("nabba.barrel.tooltip.redstone").append(Component.literal(tag.getString(REDSTONE.getName())).withStyle(ChatFormatting.YELLOW)));
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(H_FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (Level pLevel1, BlockPos pPos, BlockState pState1, T pBlockEntity) -> AbstractBarrelBlockEntity.serverTick(pLevel1, pPos, pState1, (AbstractBarrelBlockEntity) pBlockEntity);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VOID,DISCRETE,H_FACING,REDSTONE);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return pState.getValue(REDSTONE);
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        return blockEntity instanceof AbstractBarrelBlockEntity abstractBarrelBlockEntity ?
                abstractBarrelBlockEntity.getRedstoneOutput() : 0;
    }

    public BarrelType getType() {
        return type;
    }
}
