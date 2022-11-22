package tfar.nabba.item.keys;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.api.ItemMenuProvider;
import tfar.nabba.menu.ControllerKeyMenuProvider;

import java.util.List;

public class RemoteFluidControllerKeyItem extends KeyItem implements InteractsWithController {
    public RemoteFluidControllerKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MenuProvider controllerBlock) {
            savePosToKey(state, keyRing, level, pos, player);
        }
        return true;
    }

    private static void savePosToKey(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {

    }

    private static Pair<Level,BlockPos> getPosFromKey(Player player, ItemStack stack) {
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        Pair<Level, BlockPos> contPos = getPosFromKey(player,stack);
        if (!pLevel.isClientSide) {
            if (contPos != null) {
                BlockEntity blockEntity = contPos.getFirst().getBlockEntity(contPos.getSecond());
                if (blockEntity instanceof ItemMenuProvider controllerBlock) {
                    MenuProvider menuProvider = new ControllerKeyMenuProvider(controllerBlock);
                    player.openMenu(menuProvider);
                }
            }
        }
        return InteractionResultHolder.consume(stack);
    }
}
