package tfar.nabba.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.UpgradeStack;
import tfar.nabba.capability.BetterBarrelItemStackItemHandler;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BetterBarrelBlockItem extends BlockItem {
    public BetterBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public static void setStack(ItemStack container, ItemStack copyStackWithSize) {
        CompoundTag tag = copyStackWithSize.save(new CompoundTag());
        container.getOrCreateTagElement("BlockEntityTag").put(NBTKeys.Stack.name(), tag);
        container.getTagElement("BlockEntityTag").putInt(NBTKeys.RealCount.name(), copyStackWithSize.getCount());

        System.out.println(container.getTag());
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

    public static int getStorageUnits(ItemStack barrel,BarrelType type) {
        int storage = Utils.BASE_STORAGE.get(type);
        List<UpgradeStack> upgrades = getUpgrades(barrel);
        for (UpgradeStack upgradeStack : upgrades) {
            storage += upgradeStack.getStorageUnits(type);
        }
        return storage;
    }

    public static boolean isVoid(ItemStack barrel) {
        if (!barrel.hasTag())return false;
        if (barrel.getTag().contains("BlockStateTag")) {
            return barrel.getTag().getCompound("BlockStateTag").getBoolean("void");
        }
        return false;
    }

    public static boolean isItemValid(ItemStack barrel,ItemStack stack) {
        if (!stack.hasTag()) return true;
        ItemStack existing = getStoredItem(barrel);
        ItemStack ghost = getStoredItem(barrel);
        return Utils.isItemValid(existing,stack,ghost);
    }

    public static ItemStack getGhost(ItemStack barrel) {
        if (barrel.hasTag() && barrel.getTag().contains("BlockEntityTag")) {
            return ItemStack.of(barrel.getTag().getCompound("BlockEntityTag").getCompound(NBTKeys.Ghost.name()));
        }
        return ItemStack.EMPTY;
    }


    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BetterBarrelItemStackItemHandler(stack);
    }
}
