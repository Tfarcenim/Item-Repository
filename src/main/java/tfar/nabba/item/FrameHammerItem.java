package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.InteractsWithBarrel;

public class FrameHammerItem extends Item implements InteractsWithBarrel {
    public FrameHammerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        return false;
    }
}
