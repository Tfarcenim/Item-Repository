package tfar.nabba.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.UpgradeData;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

import java.util.List;

public class UpgradeItem extends Item implements InteractsWithBarrel {
    private final UpgradeData data;
    public UpgradeItem(Properties pProperties, UpgradeData data) {
        super(pProperties);
        this.data = data;
    }

    public static final String info = NABBA.MODID+".upgrade.info";
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(info,Component.literal(""+data.getSlotRequirement()).withStyle(ChatFormatting.AQUA)));
        if (data.getAdditionalStorageStacks() > 0) {
            pTooltipComponents.add(Component.translatable(getDescriptionId() + ".desc",
                    Component.literal(data.getAdditionalStorageStacks()+"").withStyle(ChatFormatting.AQUA)));
        }
        else {
            pTooltipComponents.add(Component.translatable(getDescriptionId() + ".desc"));
        }
    }

    public UpgradeData getData() {
        return data;
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player pPlayer) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
            boolean attempt = betterBarrelBlockEntity.canAcceptUpgrade(this.getData());
            if (attempt) {
                betterBarrelBlockEntity.upgrade(this.getData());
                itemstack.shrink(1);
                return true;
            }
        }
        return false;
    }
}
