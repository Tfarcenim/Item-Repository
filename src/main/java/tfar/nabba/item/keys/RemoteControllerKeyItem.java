package tfar.nabba.item.keys;

import com.mojang.datafixers.util.Pair;
import mekanism.api.NBTConstants;
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
import tfar.nabba.menu.ControllerKeyMenuProvider;

import java.util.List;

public class RemoteControllerKeyItem extends KeyItem implements InteractsWithController {
    public RemoteControllerKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ItemMenuProvider controllerBlock) {
            savePosToKey(state, keyRing, level, pos, player);
            player.sendSystemMessage(Component.literal("Successfully bound "+pos+" to key"));
        }
        return true;
    }

    private static void savePosToKey(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        keyRing.getOrCreateTag().putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        keyRing.getTag().putString("level",level.dimension().location().toString());
    }

    private static Pair<Level,BlockPos> getPosFromKey(Player player, ItemStack stack) {
        if (!stack.hasTag()) return null;
        int[] pos = stack.getTag().getIntArray("pos");
        BlockPos pos1 = new BlockPos(pos[0],pos[1],pos[2]);

        Level level = player.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY,
                new ResourceLocation(stack.getTag().getString("level"))));

        return Pair.of(level,pos1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            Pair<Level, BlockPos> contPos = getPosFromKey(player,stack);
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
