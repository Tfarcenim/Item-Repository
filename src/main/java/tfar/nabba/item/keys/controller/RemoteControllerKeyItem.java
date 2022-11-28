package tfar.nabba.item.keys.controller;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.api.ItemMenuProvider;
import tfar.nabba.item.keys.KeyItem;
import tfar.nabba.menu.ControllerKeyMenuProvider;

import java.util.List;

public class RemoteControllerKeyItem extends RemoteKeyItem {
    public RemoteControllerKeyItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public boolean handleController(BlockState state, ItemStack key, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ItemMenuProvider controllerBlock) {
            savePosToKey(state, key, level, pos, player);
            player.sendSystemMessage(Component.literal("Successfully bound "+pos+" to key"));
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        if (!level.isClientSide) {
            Pair<Level, BlockPos> contPos = getPosFromKey(level,stack);
            if (contPos != null) {
                BlockEntity blockEntity = contPos.getFirst().getBlockEntity(contPos.getSecond());
                if (blockEntity instanceof ItemMenuProvider controllerBlock) {
                    MenuProvider menuProvider = new ControllerKeyMenuProvider(controllerBlock);
                    player.openMenu(menuProvider);
                }
            }
        }
        return InteractionResultHolder.consume(stack);
    }
}
