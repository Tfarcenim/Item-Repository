package tfar.nabba.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.util.UpgradeData;

import java.util.List;

public class UpgradeItem extends Item {
    private final UpgradeData data;
    public UpgradeItem(Properties pProperties, UpgradeData data) {
        super(pProperties);
        this.data = data;
    }

    public static final String info = NABBA.MODID+".upgrade.info";
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable(info,Component.literal(""+data.getSlotRequirement()).withStyle(ChatFormatting.AQUA)));
        pTooltipComponents.add(Component.translatable(getDescriptionId()+".desc"));
    }

    public UpgradeData getData() {
        return data;
    }
}
