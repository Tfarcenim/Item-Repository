package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.block.BetterBarrelBlock;

public abstract class KeyItem extends Item {
    public KeyItem(Properties pProperties) {
        super(pProperties);
    }

    //note, cannot use onItemUse because the block's method is called BEFORE the item's method, so we have the barrel call here instead
    public abstract boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer);

}
