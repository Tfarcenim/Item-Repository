package tfar.nabba.item.keys.controller;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.item.keys.KeyItem;

import java.util.List;

public abstract class RemoteKeyItem extends KeyItem implements InteractsWithController {
    public RemoteKeyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (stack.hasTag() && level != null) {
            int[] pos = stack.getTag().getIntArray("pos");
            pTooltipComponents.add(Component.literal("Bound to ("+pos[0]+","+pos[0]+","+pos[2]+")"));
        }
    }

    protected static void savePosToKey(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        keyRing.getOrCreateTag().putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        keyRing.getTag().putString("level",level.dimension().location().toString());
    }

    protected static Pair<Level,BlockPos> getPosFromKey(Level homeLevel, ItemStack stack) {
        if (!stack.hasTag()) return null;
        int[] pos = stack.getTag().getIntArray("pos");
        BlockPos pos1 = new BlockPos(pos[0],pos[1],pos[2]);

        Level level = homeLevel.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY,
                new ResourceLocation(stack.getTag().getString("level"))));

        return Pair.of(level,pos1);
    }

}
