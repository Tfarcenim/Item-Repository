package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.BarrelFrameTier;

public class BarrelFrameUpgradeItem extends Item implements InteractsWithBarrel{
    public BarrelFrameUpgradeItem(Properties pProperties, BarrelFrameTier from,BarrelFrameTier to) {
        super(pProperties);
        //don't sidegrade or downgrade
        if (from.getTier() >= to.getTier()) {
            throw new RuntimeException(to +" must be a higher tier than "+ from);
        }
    }
    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        return false;
    }
}
