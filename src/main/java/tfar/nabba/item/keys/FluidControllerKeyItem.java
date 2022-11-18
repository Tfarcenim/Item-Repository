package tfar.nabba.item.keys;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.menu.ControllerKeyMenuProvider;
import tfar.nabba.menu.FluidControllerKeyMenuProvider;

import java.util.List;

public class FluidControllerKeyItem extends KeyItem implements InteractsWithController {
    public FluidControllerKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ControllerBlockEntity controllerBlock) {
            MenuProvider menuProvider = new FluidControllerKeyMenuProvider(controllerBlock);
            player.openMenu(menuProvider);
        }
        return true;
    }
}
