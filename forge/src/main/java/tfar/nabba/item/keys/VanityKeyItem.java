package tfar.nabba.item.keys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.menu.VanityKeyMenuProvider;

public class VanityKeyItem extends Item implements InteractsWithBarrel, InteractsWithController {
    public VanityKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractBarrelBlockEntity || blockEntity instanceof ControllerBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player,new VanityKeyMenuProvider(pos),pos);
        }
        return true;
    }

    @Override
    public boolean handleController(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        return handleBarrel(state, itemstack, level, pos, player);
    }
}
