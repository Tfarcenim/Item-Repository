package tfar.nabba.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.util.NBTKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BetterBarrelBlockItem extends BlockItem {
    public BetterBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        ItemStack disp = getStoredItem(pStack);
        return disp.isEmpty() ? super.getTooltipImage(pStack) : Optional.of(new BetterBarrelTooltip(disp));
    }

    public static ItemStack getStoredItem(ItemStack barrel) {
        if (barrel.hasTag() && barrel.getTag().contains("BlockEntityTag")) {
            ItemStack stack = ItemStack.of(barrel.getTag().getCompound("BlockEntityTag").getCompound("Stack"));
            stack.setCount(barrel.getTag().getCompound("BlockEntityTag").getInt("RealCount"));
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static List<UpgradeStack> getUpgrades(ItemStack barrel) {
        List<UpgradeStack> upgradeStacks = new ArrayList<>();
        if (barrel.hasTag() && barrel.getTag().contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = barrel.getTag().getCompound("BlockEntityTag");
            ListTag listTag = blockEntityTag.getList(NBTKeys.Upgrades.name(), Tag.TAG_COMPOUND);
            for (Tag tag : listTag) {
                upgradeStacks.add(UpgradeStack.of((CompoundTag) tag));
            }
        }
        return upgradeStacks;
    }

    public static int getUsedSlotsFromItem(ItemStack barrel) {
        int i = 0;
        for (UpgradeStack stack : getUpgrades(barrel)) {
            i += stack.getUpgradeSlotsRequired();
        }
        return i;
    }

}
