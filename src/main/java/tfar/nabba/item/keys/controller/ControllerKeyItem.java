package tfar.nabba.item.keys.controller;

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
import tfar.nabba.api.DisplayType;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.api.DisplayMenuProvider;
import tfar.nabba.item.keys.KeyItem;
import tfar.nabba.menu.ControllerKeyMenuProvider;

import java.util.List;

public class ControllerKeyItem extends KeyItem implements InteractsWithController {
    protected final DisplayType type;

    public ControllerKeyItem(Properties pProperties, DisplayType type) {
        super(pProperties);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, level, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DisplayMenuProvider controllerBlock) {
            MenuProvider menuProvider = new ControllerKeyMenuProvider(controllerBlock,type);
                player.openMenu(menuProvider);
        }
        return true;
    }
}
