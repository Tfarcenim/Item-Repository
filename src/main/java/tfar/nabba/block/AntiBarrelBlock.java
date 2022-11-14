package tfar.nabba.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
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
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.item.InteractsWithBarrel;
import tfar.nabba.util.BarrelType;

import java.util.List;

public class AntiBarrelBlock extends AbstractBarrelBlock {
    public AntiBarrelBlock(Properties pProperties, BarrelFrameTier barrelFrameTier) {
        super(pProperties, BarrelType.ANTI, barrelFrameTier);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            Item item = stack.getItem();
            if (item instanceof InteractsWithBarrel interactsWithBarrel && interactsWithBarrel.handleBarrel(state, stack, world, pos, player)) {
                return InteractionResult.CONSUME;
            } else {

                MenuProvider menuProvider = state.getMenuProvider(world, pos);
                if (menuProvider != null) {
                    player.openMenu(menuProvider);
                    PiglinAi.angerNearbyPiglins(player, true);
                }
                return InteractionResult.CONSUME;
            }
        }
        return  InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    public void appendBlockStateInfo(ItemStack stack, List<Component> tooltip) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag().getCompound("BlockStateTag");
            if (!tag.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.literal("Void: ").append(Component.literal(tag.getString(VOID.getName())).withStyle(ChatFormatting.YELLOW)));
            }
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
        return new AntiBarrelBlockEntity(pPos, pState);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pLevel.isClientSide) return;
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof AntiBarrelBlockEntity antiBarrelBlockEntity) {
            if (pStack.hasCustomHoverName()) {
                antiBarrelBlockEntity.setCustomName(pStack.getHoverName());
            }
            antiBarrelBlockEntity.initialize(pStack);
        }
    }
}
