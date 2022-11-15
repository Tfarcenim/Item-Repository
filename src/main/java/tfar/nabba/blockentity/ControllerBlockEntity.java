package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.tag.ModBlockTags;
import tfar.nabba.api.InteractsWithBarrel;
import tfar.nabba.menu.ControllerKeyMenu;
import tfar.nabba.util.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ControllerBlockEntity extends BlockEntity implements MenuProvider {
    public String search = "";

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return barrels.size();
                case 1:
                    return 1;//getInventory().getFullSlots(search);
                default:
                    return 0;
            }
        }

        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0:
            }
        }

        public int getCount() {
            return 2;
        }
    };

    private final List<BlockPos> barrels = new ArrayList<>();
    private final List<BlockPos> invalid = new ArrayList<>();

    protected ControllerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        controllerHandler = new ControllerHandler(this);
    }

    private final ControllerHandler controllerHandler;


    protected final int[] syncSlots = new int[54];

    private static void defaultDisplaySlots(int[] ints) {
        for (int i = 0; i <  ints.length;i++) {
            ints[i] = i;
        }
    }
    protected final ContainerData syncSlotsAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return syncSlots[pIndex];
        }

        @Override
        public void set(int pIndex, int pValue) {
            syncSlots[pIndex] = pValue;
        }

        @Override
        public int getCount() {
            return syncSlots.length;
        }
    };


    public static ControllerBlockEntity create(BlockPos pos, BlockState state) {
        return new ControllerBlockEntity(ModBlockEntityTypes.CONTROLLER, pos, state);
    }

    public void gatherBarrels() {
        BlockPos thisPos = getBlockPos();
        List<BlockEntity> betterBarrelBlockEntities = Utils.getNearbyBarrels(level,thisPos);
        for (BlockEntity abstractBarrelBlockEntity : betterBarrelBlockEntities) {
            addBarrel(abstractBarrelBlockEntity.getBlockPos());
        }
    }

    public void addBarrel(BlockPos pos) {
        barrels.add(pos);
        setChanged();
    }

    public void removeBarrel(BlockPos pos) {
        barrels.remove(pos);
        setChanged();
    }

    @Nullable
    public BlockEntity getBE(int i) {
        if (i >= barrels.size()) {
            return null;
        }
        return getBE(barrels.get(i));
    }

    @Nullable
    public BlockEntity getBE(BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BetterBarrelBlockEntity) {
            return blockEntity;
        }
        invalid.add(pos);
        return null;
    }

    public boolean validBarrel(BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BetterBarrelBlockEntity;
    }

    public void interactWithBarrel(BlockPos pos) {

    }

    public List<BlockPos> getBarrels() {
        return barrels;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ListTag upgradesTag = new ListTag();

        for (BlockPos stack : barrels) {
            CompoundTag tag = new CompoundTag();
            tag.putIntArray("pos",new int[]{stack.getX(),stack.getY(),stack.getZ()});
            upgradesTag.add(tag);
        }
        pTag.put("Barrels", upgradesTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        barrels.clear();
        ListTag upgradesTag = pTag.getList("Barrels", Tag.TAG_COMPOUND);
        for (Tag tag : upgradesTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            int[] pos = compoundTag.getIntArray("pos");
            barrels.add(new BlockPos(pos[0],pos[1],pos[2]));
        }
    }

    public ControllerHandler getBarrelHandler() {
        return controllerHandler;
    }

    public void interactWithBarrels(ItemStack stack, Player player) {
        InteractsWithBarrel interactsWithBarrel = (InteractsWithBarrel) stack.getItem();
        for (BlockPos pos : getBarrels()) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlockTags.BETTER_BARRELS)) {
                interactsWithBarrel.handleBarrel(state,stack,level,pos,player);
            } else {
                invalid.add(pos);
            }
        }
        clearInvalid();
    }

    private void clearInvalid() {
        invalid.forEach(barrels::remove);
        invalid.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ControllerKeyMenu(pContainerId,pPlayerInventory, ContainerLevelAccess.create(level,getBlockPos()),controllerHandler,dataAccess,syncSlotsAccess);
    }

    public static class ControllerHandler implements IItemHandler {
        private final ControllerBlockEntity controllerBlockEntity;

        ControllerHandler(ControllerBlockEntity controllerBlockEntity) {
            this.controllerBlockEntity = controllerBlockEntity;
        }

        @Override
        public int getSlots() {
            return controllerBlockEntity.barrels.size();
        }

        public ItemStack universalAddItem(ItemStack stack) {
            ItemStack remainder = stack;

            for (int i = 0; i < getSlots();i++) {
                remainder = insertItem(i,remainder,false);
                if (remainder.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
            return remainder;
        }

        public List<Integer> getDisplaySlots(int row,String search) {
            List<Integer> disp = new ArrayList<>();
            int countForDisplay = 0;
            int index = 0;
            int startPos = 9 * row;
            while (countForDisplay < 54) {
                ItemStack stack = getStackInSlot(startPos + index);
                if (matches(stack,search)) {
                    disp.add(startPos + index);
                    countForDisplay++;
                } else if (stack.isEmpty()) {
                    break;
                }
                index++;
            }
            return disp;
        }

        public boolean matches(ItemStack stack,String search) {
            if (search.isEmpty()) {
                return true;
            } else {
                Item item = stack.getItem();
                if (search.startsWith("#")) {
                    String sub = search.substring(1);

                    List<TagKey<Item>> tags = item.builtInRegistryHolder().tags().toList();
                    for (TagKey<Item> tag : tags) {
                        if (tag.location().getPath().startsWith(sub)) {
                            return true;
                        }
                    }
                    return false;
                } else if (Registry.ITEM.getKey(item).getPath().startsWith(search)) {
                    return true;
                }
            }
            return false;
        }

        public void computeSlots() {
            int i = 0;
            for (BlockPos pos : controllerBlockEntity.barrels) {
                BlockEntity blockEntity = controllerBlockEntity.getBE(pos);
                if (blockEntity instanceof AbstractBarrelBlockEntity abstractBarrelBlockEntity) {
                    i+= abstractBarrelBlockEntity.getItemHandler().getSlots();
                }
            }
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            if (slot >= getSlots()) return ItemStack.EMPTY;
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().getStackInSlot(0);
            } else {
                controllerBlockEntity.clearInvalid();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || !isItemValid(slot, stack)) return stack;

            BlockEntity blockEntity = controllerBlockEntity.getBE(slot);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().insertItem(0,stack,simulate);
            } else {
                controllerBlockEntity.clearInvalid();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0|| slot >= getSlots()) return ItemStack.EMPTY;

            BlockEntity blockEntity = controllerBlockEntity.getBE(slot);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().extractItem(0,amount,simulate);
            } else {
                controllerBlockEntity.clearInvalid();
            }
            return ItemStack.EMPTY;
        }
        @Override
        public int getSlotLimit(int slot) {
            if (slot >= getSlots()) return 0;
            BlockEntity blockEntity = controllerBlockEntity.getBE(slot);
            if (blockEntity instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                return barrelBlockEntity.getItemHandler().getSlotLimit(0);
            } else {
                controllerBlockEntity.clearInvalid();
            }
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack incoming) {
            if (slot < getSlots()) {
                if (controllerBlockEntity.getBE(slot) instanceof BetterBarrelBlockEntity barrelBlockEntity) {
                    return barrelBlockEntity.getItemHandler().isItemValid(0, incoming);
                } else {
                    controllerBlockEntity.clearInvalid();
                }
            }
            return false;
        }

        public void markDirty() {
            controllerBlockEntity.setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private LazyOptional<IItemHandler> optional = LazyOptional.of(this::getBarrelHandler);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? optional.cast() : super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optional.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        optional = LazyOptional.of(this::getBarrelHandler);
    }
}
