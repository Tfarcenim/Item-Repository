package tfar.nabba.item.keys.controller;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.api.FluidMenuProvider;
import tfar.nabba.menu.FluidControllerKeyMenuProvider;

public class RemoteFluidControllerKeyItem extends RemoteKeyItem {
    public RemoteFluidControllerKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FluidMenuProvider controllerBlock) {
            savePosToKey(state, keyRing, level, pos, player);
            player.sendSystemMessage(Component.literal("Successfully bound "+pos+" to key"));
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide) {
            Pair<Level, BlockPos> contPos = getPosFromKey(player.level,stack);
            if (contPos != null) {
                BlockEntity blockEntity = contPos.getFirst().getBlockEntity(contPos.getSecond());
                if (blockEntity instanceof FluidMenuProvider controllerBlock) {
                    MenuProvider menuProvider = new FluidControllerKeyMenuProvider(controllerBlock);
                    player.openMenu(menuProvider);
                }
            }
        }
        return InteractionResultHolder.consume(stack);
    }
}
