package tfar.nabba.item.keys;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.init.tag.ModItemTags;

import java.util.ArrayList;
import java.util.List;

public class KeyRingItem extends Item implements InteractsWithBarrel, InteractsWithController {
    public KeyRingItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        ItemStack key = getSelectedKey(keyRing);
        if (key.getItem() instanceof InteractsWithBarrel keyItem) {//nbt shouldn't be important
            return keyItem.handleBarrel(state, key, level, pos, player);
        }
        return true;
    }

    protected static List<ItemStack> getKeys(ItemStack stack) {
        List<ItemStack> keys = new ArrayList<>();

        if (!stack.hasTag()) return keys;

        ListTag listTag = stack.getTag().getList("Keys", Tag.TAG_COMPOUND);

        for (Tag tag : listTag) {
            CompoundTag stringTag = (CompoundTag) tag;
            ItemStack item = ItemStack.of(stringTag);
            if (!item.isEmpty()) {
                keys.add(item);
            }
        }
        return keys;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, level, pTooltipComponents, pIsAdvanced);

        List<ItemStack> keys = getKeys(stack);
        for (ItemStack item : keys) {
            pTooltipComponents.add(((MutableComponent) item.getHoverName()).withStyle(ChatFormatting.GRAY));
            item.getItem().appendHoverText(item, level, pTooltipComponents, pIsAdvanced);
        }
    }

    protected boolean addKey(ItemStack keyRing, ItemStack key) {
        ItemStack singleKey = ItemHandlerHelper.copyStackWithSize(key,1);//don't modify the original key
        List<ItemStack> keys = getKeys(keyRing);

        for (ItemStack stack : keys) {
            if (stack.getItem() == singleKey.getItem())
                return false;
        }

        CompoundTag tag = keyRing.getOrCreateTag();
        ListTag listTag = tag.getList("Keys", Tag.TAG_COMPOUND);
        if (listTag.isEmpty()) {
            tag.putInt("Selected", 0);
        }
        listTag.add(singleKey.save(new CompoundTag()));
        tag.put("Keys", listTag);
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStack selected = getSelectedKey(stack);
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

        List<ItemStack> keys = getKeys(keyRing);

        ItemStack key = getSelectedKey(keyRing);

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
                            if (addKey(keyRing, stack)) {
                                stack.shrink(1);
                            }
                        }
                    }
                }
            }
        }
        return super.use(level, player, hand);
    }

    private static final String SEL = "Selected";

    public static void scrollKey(ItemStack keyRing, boolean up) {
        List<ItemStack> keys = getKeys(keyRing);
        if (keys.isEmpty()) return;
        int selected = keyRing.getTag().getInt(SEL);
        if (selected >= keys.size() - 1 && !up) {
            keyRing.getTag().putInt(SEL, 0);
        } else if (selected <= 0 && up) {
            keyRing.getTag().putInt(SEL, keys.size() - 1);
        } else {
            keyRing.getTag().putInt(SEL, up ? --selected : ++selected);
        }
    }

    public static boolean hasAnyKeys(ItemStack keyRing) {
        return !getKeys(keyRing).isEmpty();
    }

    public static ItemStack getSelectedKey(ItemStack keyRing) {
        if (!keyRing.hasTag())
            return ItemStack.EMPTY;
        List<ItemStack> keys = getKeys(keyRing);
        int selected = keyRing.getTag().getInt(SEL);
        //crash mitigation
        if (selected < 0 || selected >= keys.size()) {
            keyRing.getTag().remove(SEL);
            selected = 0;
        }
        return keys.get(selected);
    }

    public static void saveKeyChanged(ItemStack keyRing, ItemStack key) {
        int selected = keyRing.getOrCreateTag().getInt(SEL);
        ListTag listTag = keyRing.getTag().getList("Keys", Tag.TAG_COMPOUND);
        listTag.set(selected,key.save(new CompoundTag()));
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player player) {
        ItemStack key = getSelectedKey(keyRing);
        if (key.getItem() instanceof InteractsWithController interactsWithController) {//nbt is important!
            boolean b = interactsWithController.handleController(state, key, level, pos, player);
            saveKeyChanged(keyRing, key);
            return b;
        }
        return false;
    }
}
