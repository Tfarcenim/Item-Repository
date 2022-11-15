package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.blockentity.ControllerBlockEntity;

import java.util.List;

public interface InteractsWithController {

    //note, cannot use onUse because the block's method is called BEFORE the item's method, so we have the controller call here instead
    boolean handleController(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer);

    default void interactWithBarrels(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ControllerBlockEntity controller) {
            controller.interactWithBarrels(itemstack,player);
        }
    }
}
