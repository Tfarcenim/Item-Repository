package tfar.nabba.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import tfar.nabba.NABBA;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.Utils;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.inventory.RepositoryInventoryInputWrapper;
import tfar.nabba.inventory.RepositoryInventoryOutputWrapper;
import tfar.nabba.world.RepositoryInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AntiBarrelBlockEntity extends AbstractBarrelBlockEntity implements MenuProvider {

    public CompoundTag settings = new CompoundTag();
    private Component customName;
    public LazyOptional<IItemHandler> fullOptional = LazyOptional.of(this::getInventory);
    public LazyOptional<IItemHandler> inputOptional = LazyOptional.of(() -> new RepositoryInventoryInputWrapper(getInventory()));
    public LazyOptional<IItemHandler> outputOptional = LazyOptional.of(() -> new RepositoryInventoryOutputWrapper(getInventory()));

    public String search = "";

    private ItemStack last = ItemStack.EMPTY;
    private int clientStored;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int pIndex) {
            switch (pIndex) {
                case 0:
                    return getInventory().getFullSlots(search);
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
            return 1;
        }
    };

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

    public RepositoryInventory getInventory() {
        return NABBA.instance.data.getInventory(settings.getInt(Utils.ID));
    }

    public RepositoryInventory getOrCreateInventory() {
        return NABBA.instance.data.getOrCreateInventory(settings.getInt(Utils.ID));
    }

    @Override
    public ItemStack tryAddItem(ItemStack stack) {
        RepositoryInventory repositoryInventory = getInventory();
        if (getInventory().isFull()) {
            return stack;
        }
        //attempt to add the item to the last slot
        return getInventory().insertItem(repositoryInventory.getSlots() - 1,stack,false);
    }

    public AntiBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        defaultDisplaySlots(syncSlots);
    }

    public AntiBarrelBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.ANTI_BARREL,pos,state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Anti Barrel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AntiBarrelMenu(pContainerId,pPlayerInventory, ContainerLevelAccess.create(level,getBlockPos()), getInventory(), dataAccess, syncSlotsAccess);
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return fullOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fullOptional.invalidate();
    }

    @Override
    public IItemHandler getItemHandler() {
        return getInventory();
    }

    public void saveAdditional(CompoundTag tag) {
        tag.put(NBTKeys.Settings.name(), settings);
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        tag.putInt("Stored", clientStored);
        tag.put("Last",last.save(new CompoundTag()));
        super.saveAdditional(tag);
    }
    @Override//read
    public void load(CompoundTag tag) {
        settings = tag.getCompound(NBTKeys.Settings.name());
        if (tag.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
        clientStored = tag.getInt("Stored");
        last = ItemStack.of(tag.getCompound("Last"));
        super.load(tag);
    }

    public void setCustomName(Component hoverName) {
        customName = hoverName;
    }

    /**
     * note:this is called AFTER initialize
     */
    @Override
    public void onLoad() {
        super.onLoad();
        RepositoryInventory repositoryInventory = getOrCreateInventory();
        repositoryInventory.setBlockEntity(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        clientStored = getInventory().getActualStoredCount();
    }

    public ItemStack getLastStack() {
            return last;
    }

    public void setLastStack(ItemStack stack) {
        last = stack;
    }

    public int getClientStored() {
        return clientStored;
    }

    //make sure a new id is set if there isn't one and create an inventory for it so the game doesn't freak out
    public void initialize(ItemStack stack) {
        if (!settings.contains(Utils.ID)) {
            int newId = NABBA.instance.data.getNextID();
            settings.putInt(Utils.ID,newId);
            getOrCreateInventory();
            setChanged();
        }
    }
}
