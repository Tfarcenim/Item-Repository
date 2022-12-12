package tfar.nabba.item.keys.controller;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
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
import tfar.nabba.api.DisplayMenuProvider;
import tfar.nabba.api.DisplayType;
import tfar.nabba.menu.ControllerKeyMenuProvider;

import java.util.List;

public class RemoteControllerKeyItem extends ControllerKeyItem {

    public RemoteControllerKeyItem(Properties pProperties, DisplayType type) {
        super(pProperties,type);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, level, pTooltipComponents, pIsAdvanced);
        if (stack.hasTag() && level != null) {
            int[] pos = stack.getTag().getIntArray("pos");
            pTooltipComponents.add(Component.translatable(getDescriptionId() +".tooltip",pos[0],pos[1],pos[2]));
        }
    }

    protected static void savePosToKey(ItemStack keyRing, Level level, BlockPos pos) {
        keyRing.getOrCreateTag().putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        keyRing.getTag().putString("level",level.dimension().location().toString());
    }

    protected static Pair<Level,BlockPos> getPosFromKey(Level homeLevel, ItemStack stack) {
        if (!stack.hasTag()) return null;
        int[] pos = stack.getTag().getIntArray("pos");
        BlockPos pos1 = new BlockPos(pos[0],pos[1],pos[2]);

        Level level = homeLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION,
                new ResourceLocation(stack.getTag().getString("level"))));

        return Pair.of(level,pos1);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack key, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DisplayMenuProvider) {
            savePosToKey(key, level, pos);
            player.sendSystemMessage(Component.translatable("nabba.remote_key.message.bind_success",pos));
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
                if (blockEntity instanceof DisplayMenuProvider controllerBlock) {
                    MenuProvider menuProvider = new ControllerKeyMenuProvider(controllerBlock, type);
                    player.openMenu(menuProvider);
                }
            }
        }
        return InteractionResultHolder.consume(stack);
    }
}
