package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.blockentity.ControllerBlockEntity;

public class NetworkVisualizerItem extends Item implements InteractsWithController {
    public NetworkVisualizerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ControllerBlockEntity controllerBlockEntity) {
            controllerBlockEntity.storeNetworkInfo(itemstack);
            return true;
        }

        return false;
    }
}
