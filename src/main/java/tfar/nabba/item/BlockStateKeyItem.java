package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import tfar.nabba.block.BetterBarrelBlock;

public class BlockStateKeyItem extends KeyItem {
    private final BooleanProperty property;

    public BlockStateKeyItem(Properties pProperties, BooleanProperty property) {
        super(pProperties);
        this.property = property;
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer) {
        BlockState newState = state.setValue(property,!state.getValue(property));
        level.setBlock(pos,newState,3);
        level.sendBlockUpdated(pos,state,newState,3);
        return true;
    }
}
