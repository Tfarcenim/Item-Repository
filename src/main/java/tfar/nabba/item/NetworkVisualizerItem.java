package tfar.nabba.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (stack.getTagElement(ControllerBlockEntity.NET_INFO) != null && !pLevel.isClientSide) {
            CompoundTag tag = stack.getTagElement(ControllerBlockEntity.NET_INFO);
            int[] posCont = tag.getIntArray("controller");
            BlockPos pos = new BlockPos(posCont[0],posCont[1],posCont[2]);
            BlockEntity blockEntity = pLevel.getBlockEntity(pos);
            if (blockEntity instanceof ControllerBlockEntity controllerBlockEntity) {
                controllerBlockEntity.storeNetworkInfo(stack);
            } else {
                pPlayer.sendSystemMessage(Component.translatable("No controller present at %s",pos));
                stack.removeTagKey(ControllerBlockEntity.NET_INFO);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }
}
