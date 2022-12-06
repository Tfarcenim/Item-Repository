package tfar.nabba.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.datagen.providers.ModRecipeProvider;

import java.util.List;

public class BarrelHammerItem extends Item implements InteractsWithBarrel {
    public BarrelHammerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        AbstractBarrelBlock abstractBarrelBlock = (AbstractBarrelBlock) state.getBlock();
        BarrelFrameTier barrelFrameTier = abstractBarrelBlock.getBarrelTier();
        if (barrelFrameTier.getTier() > 0) {
            BarrelFrameTier below = BarrelFrameTier.tiers.get(barrelFrameTier.getTier() - 1);
            BarrelFrameUpgradeItem barrelFrameUpgradeItem = ModRecipeProvider.lookupPair(Pair.of(below.getTier(), barrelFrameTier.getTier()));
            if (barrelFrameUpgradeItem != null) {
                AbstractBarrelBlockEntity abstractBarrelBlockEntity = (AbstractBarrelBlockEntity) level.getBlockEntity(pos);
                if (canDowngrade(below,abstractBarrelBlockEntity,player)) {
                    BarrelFrameUpgradeItem.copyBlockStatesAndData(
                            state,below.getBarrel(abstractBarrelBlockEntity.getBarrelType()).defaultBlockState(),level,pos);
                    ItemHandlerHelper.giveItemToPlayer(player, barrelFrameUpgradeItem.getDefaultInstance());
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            player.sendSystemMessage(Component.translatable("nabba.barrel_hammer.message.no_downgrade"));
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable(getDescriptionId() +".tooltip"));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        return super.useOn(pContext);
    }

    public static boolean canDowngrade(BarrelFrameTier target, AbstractBarrelBlockEntity abstractBarrelBlockEntity, Player player) {
        int currentPoints = abstractBarrelBlockEntity.getUsedSlots();
        int capacity = target.getUpgradeSlots();

        if (capacity < currentPoints) {
            player.sendSystemMessage(Component.translatable("nabba.barrel_hammer.message.remove_upgrade"));
            return false;
        }
        return true;
    }
}
