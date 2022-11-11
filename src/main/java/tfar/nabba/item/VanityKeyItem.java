package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.menu.VanityKeyMenuProvider;

public class VanityKeyItem extends KeyItem {
    public VanityKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BetterBarrelBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer,new VanityKeyMenuProvider(pos),pos);
        }
        return true;
    }
}
