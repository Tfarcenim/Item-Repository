package tfar.nabba.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import tfar.nabba.platform.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CommonUtils {

    public static final int INVALID = -1;
    public static final String INFINITY = "\u221E";

    public static final int RADIUS = 13;

    public static final double DEFAULT_SIZE = .5;
    public static final int DEFAULT_COLOR = 0xff88ff;

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    public static final String SEL = "Selected";

    public static boolean isItemValid(ItemStack existing, @NotNull ItemStack incoming, ItemStack ghost) {
        return (ghost.isEmpty() || ItemStack.isSameItemSameTags(incoming, ghost))
                && (existing.isEmpty() || ItemStack.isSameItemSameTags(existing, incoming));
    }

    public static String formatLargeNumber(long number) {
        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

    public static List<ItemStackWrapper> wrap(List<ItemStack> stacks) {
        return stacks.stream().map(ItemStackWrapper::new).toList();
    }

    public static void merge(List<ItemStack> stacks, ItemStack toMerge) {
        for (ItemStack stack : stacks) {
            if (canItemStacksStack(stack, toMerge)) {
                int grow = Math.min(Integer.MAX_VALUE - stack.getCount(), toMerge.getCount());
                if (grow > 0) {
                    stack.grow(grow);
                    toMerge.shrink(grow);
                }
            }
        }
        if (!toMerge.isEmpty()) {
            stacks.add(toMerge);
        }
    }

    public static int[] getArray(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    //forge has to check caps
    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        return Services.PLATFORM.canItemStacksStack(a,b);
    }

    public static ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size) {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

    //searches a 3x3 chunk area
    public static List<BlockEntity> getNearbyBlockEntities(Level level, Predicate<BlockEntity> predicate, BlockPos thisPos) {

        int chunkX = SectionPos.blockToSectionCoord(thisPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(thisPos.getZ());
        List<BlockEntity> blockentities = new ArrayList<>();
        for (int z = -1; z <= 1; z++) {
            for (int x = -1; x <= 1; x++) {
                LevelChunk chunk = level.getChunk(chunkX + x, chunkZ + z);
                Map<BlockPos, BlockEntity> blockEntities = chunk.getBlockEntities();
                for (Map.Entry<BlockPos, BlockEntity> entry : blockEntities.entrySet()) {
                    BlockEntity blockEntity = entry.getValue();
                    if (predicate.test(blockEntity)) {
                        BlockPos pos = entry.getKey();
                        if (
                                Math.abs(pos.getX() - thisPos.getX()) < RADIUS
                                        && Math.abs(pos.getY() - thisPos.getY()) < RADIUS
                                        && Math.abs(pos.getZ() - thisPos.getZ()) < RADIUS
                        ) {
                            blockentities.add(blockEntity);
                        }
                    }
                }
            }
        }
        return blockentities;
    }

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

    public static List<ItemStack> getKeys(ItemStack stack) {
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

    public static boolean addKey(ItemStack keyRing, ItemStack key) {
        ItemStack singleKey = copyStackWithSize(key,1);//don't modify the original key
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


    /**
     * Inserts the given itemstack into the players inventory.
     * If the inventory can't hold it, the item will be dropped in the world at the players position.
     *
     * @param player The player to give the item to
     * @param stack  The itemstack to insert
     */
    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack)
    {
        if (stack.isEmpty()) return;

        Inventory inventory = player.getInventory();

        List<ItemStack> main = inventory.items;

        Level level = player.level();

        // try adding it into the inventory
        ItemStack remainder = stack;


        inventory.placeItemBackInInventory(stack);

        // insert into preferred slot first
        // then into the inventory in general
      /*  if (!remainder.isEmpty())
        {
            remainder = insertItemStacked(inventory, remainder, false);
        }

        // play sound if something got picked up
        if (remainder.isEmpty() || remainder.getCount() != stack.getCount())
        {
            level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        // drop remaining itemstack into the level
        if (!remainder.isEmpty() && !level.isClientSide)
        {
            ItemEntity entityitem = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
            entityitem.setPickUpDelay(40);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

            level.addFreshEntity(entityitem);
        }*/
    }
}
