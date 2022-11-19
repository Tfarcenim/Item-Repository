package tfar.nabba.item.keys;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.api.InteractsWithController;
import tfar.nabba.init.tag.ModItemTags;

import java.util.ArrayList;
import java.util.List;

public class KeyRingItem extends KeyItem implements InteractsWithBarrel, InteractsWithController {
    public KeyRingItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean handleBarrel(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player pPlayer) {
        Item key = getSelectedKey(keyRing);
        if (key instanceof InteractsWithBarrel keyItem) {//nbt shouldn't be important
            return keyItem.handleBarrel(state, keyRing, level, pos, pPlayer);
        }
        return true;
    }

    protected static List<Item> getKeys(ItemStack stack) {
        List<Item> keys = new ArrayList<>();

        if (!stack.hasTag()) return keys;

        ListTag listTag = stack.getTag().getList("Keys", Tag.TAG_STRING);

        for (Tag tag : listTag) {
            StringTag stringTag = (StringTag)tag;
            Item item = Registry.ITEM.get(new ResourceLocation(stringTag.getAsString()));
            if (item != Items.AIR) {
                keys.add(item);
            }
        }
        return keys;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        List<Item> keys = getKeys(pStack);
        for (Item item : keys) {
            pTooltipComponents.add(((MutableComponent)item.getName(item.getDefaultInstance())).withStyle(ChatFormatting.GRAY));
        }
    }

    protected boolean addKey(ItemStack keyRing, Item key) {
        List<Item> keys = getKeys(keyRing);
        if (keys.contains(key)) {
            return false;
        }

        CompoundTag tag = keyRing.getOrCreateTag();
        ListTag listTag = tag.getList("Keys",Tag.TAG_STRING);
        if (listTag.isEmpty()) {
            tag.putInt("Selected",0);
        }
        listTag.add(StringTag.valueOf(Registry.ITEM.getKey(key).toString()));
        tag.put("Keys",listTag);
        return true;
    }

    @Override
    public Component getName(ItemStack pStack) {
        Item selected = getSelectedKey(pStack);
        if (selected == Items.AIR) {
            return super.getName(pStack);
        }

        Component base = super.getName(pStack);
        Component selectedC = selected.getName(new ItemStack(selected));

        return Component.translatable("nabba.key_ring.selected_key",base,selectedC);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand hand) {
        ItemStack keyRing = player.getItemInHand(hand);

        List<Item> keys = getKeys(keyRing);
        if (!pLevel.isClientSide) {
            //try to gather all keys the player has
            if (player.isCrouching()) {
                Inventory inventory = player.getInventory();
                NonNullList<ItemStack> main = inventory.items;

                for (ItemStack stack : main) {
                    if (stack.is(ModItemTags.KEYS)) {
                        if (!keys.contains(stack.getItem())) {
                           if(addKey(keyRing,stack.getItem())) {
                                stack.shrink(1);
                            }
                        }
                    }
                }
            }
        }
        return super.use(pLevel, player, hand);
    }
    public static void scrollKey(ItemStack keyRing, boolean up) {
        List<Item> keys = getKeys(keyRing);
        if (keys.isEmpty())return;
        int selected = keyRing.getTag().getInt("Selected");
        if (selected >= keys.size() - 1 && !up) {
            keyRing.getTag().putInt("Selected",0);
        } else if (selected <= 0 && up){
            keyRing.getTag().putInt("Selected",keys.size() - 1);
        } else {
            keyRing.getTag().putInt("Selected", up ? --selected : ++selected);
        }
    }

    public static boolean hasAnyKeys(ItemStack keyRing) {
        return !getKeys(keyRing).isEmpty();
    }

    public static Item getSelectedKey(ItemStack keyRing) {
        if (!keyRing.hasTag())
            return Items.AIR;

        List<Item> keys = getKeys(keyRing);
        int selected = keyRing.getTag().getInt("Selected");
        //crash mitigation
        if (selected < 0 || selected >= keys.size()) {
            keyRing.getTag().remove("Selected");
            selected = 0;
        }
        return keys.get(selected);
    }

    @Override
    public boolean handleController(BlockState state, ItemStack keyRing, Level level, BlockPos pos, Player pPlayer) {
        Item key = getSelectedKey(keyRing);
        if (key instanceof InteractsWithController interactsWithController) {//nbt shouldn't be important
            return interactsWithController.handleController(state, keyRing, level, pos, pPlayer);
        }
        return false;
    }
}
