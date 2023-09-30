package tfar.nabba.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.util.BarrelType;

import java.util.List;

public class StorageUpgradeItem extends UpgradeItem {
    private final BarrelType type;

    public static StorageUpgradeItem better(Properties properties,UpgradeStack stack) {
        return new StorageUpgradeItem(properties,stack,BarrelType.BETTER);
    }

    public static StorageUpgradeItem anti(Properties properties,UpgradeStack stack) {
        return new StorageUpgradeItem(properties,stack,BarrelType.ANTI);
    }

    public static StorageUpgradeItem fluid(Properties properties,UpgradeStack stack) {
        return new StorageUpgradeItem(properties,stack,BarrelType.FLUID);
    }
    public StorageUpgradeItem(Properties pProperties, UpgradeStack data, BarrelType type) {
        super(pProperties,data);
        this.type = type;
    }

    public static final String info = NABBA.MODID+".upgrade.info";
    public static final String info1 = NABBA.MODID+".upgrade.info1";

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(info,Component.literal(""+data.getUpgradeSlotsRequired()).withStyle(ChatFormatting.AQUA)));
        pTooltipComponents.add(Component.translatable(info1,Component.literal(""+data.getMaxPermitted()).withStyle(ChatFormatting.AQUA)));
        pTooltipComponents.add(Component.translatable(getDescriptionId() + ".tooltip",
                Component.literal(data.getStorageMultiplier()+"").withStyle(ChatFormatting.AQUA)));
    }

    public BarrelType getType() {
        return type;
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack itemstack, Level level, BlockPos pos, Player player) {
        if (itemstack.isEmpty()) return false;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractBarrelBlockEntity betterBarrelBlockEntity) {
            boolean attempt = betterBarrelBlockEntity.isValid(this) && betterBarrelBlockEntity.canAcceptUpgrade(this.getDataStack());
            if (attempt) {
                betterBarrelBlockEntity.upgrade(this.getDataStack());
                itemstack.shrink(1);
                return true;
            }
        }
        return false;
    }
}
