package tfar.nabba.item.keys;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.init.tag.ModItemTags;
import tfar.nabba.util.CommonUtils;

import java.util.List;

public class KeyRingItem extends Item implements InteractsWithBarrel, InteractsWithController {
    public KeyRingItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        ItemStack key = CommonUtils.getSelectedKey(keyRing);
        if (key.getItem() instanceof InteractsWithBarrel keyItem) {//nbt shouldn't be important
            return keyItem.handleBarrel(state, key, level, pos, player);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, level, pTooltipComponents, pIsAdvanced);

        List<ItemStack> keys = CommonUtils.getKeys(stack);
        for (ItemStack item : keys) {
            pTooltipComponents.add(((MutableComponent) item.getHoverName()).withStyle(ChatFormatting.GRAY));
            item.getItem().appendHoverText(item, level, pTooltipComponents, pIsAdvanced);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStack selected = CommonUtils.getSelectedKey(stack);
        if (selected.isEmpty()) {
            return super.getName(stack);
        }

        Component base = super.getName(stack);
        Component selectedC = selected.getHoverName();

        return Component.translatable("nabba.key_ring.selected_key", base, selectedC);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack keyRing = player.getItemInHand(hand);

        List<ItemStack> keys = CommonUtils.getKeys(keyRing);

        ItemStack key = CommonUtils.getSelectedKey(keyRing);

        if (!key.isEmpty()) {
            player.setItemInHand(hand,key);
            key.use(level, player, hand);
            player.setItemInHand(hand,keyRing);
        } else if (!level.isClientSide) {
            //try to gather all keys the player has
            if (player.isCrouching()) {
                Inventory inventory = player.getInventory();
                NonNullList<ItemStack> main = inventory.items;

                for (ItemStack stack : main) {
                    if (stack.is(ModItemTags.KEYS)) {
                        if (!keys.contains(stack)) {
                            if (CommonUtils.addKey(keyRing, stack)) {
                                stack.shrink(1);
                            }
                        }
                    }
                }
            }
        }
        return super.use(level, player, hand);
    }

    public static boolean hasAnyKeys(ItemStack keyRing) {
        return !CommonUtils.getKeys(keyRing).isEmpty();
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        ItemStack key = CommonUtils.getSelectedKey(keyRing);
        if (key.getItem() instanceof InteractsWithController interactsWithController) {//nbt is important!
            boolean b = interactsWithController.handleController(state, key, level, pos, player);
            CommonUtils.saveKeyChanged(keyRing, key);
            return b;
        }
        return false;
    }
}
