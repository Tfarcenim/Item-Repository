package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.inventory.FakeSlotSynchronizer;

import java.util.List;

public abstract class SearchableMenu extends AbstractContainerMenu {
    protected final Inventory inventory;
    private final ContainerLevelAccess access;
    private final DataSlot row = DataSlot.standalone();
    private final ContainerData syncSlots;
    private final ContainerData inventoryData;

    private FakeSlotSynchronizer fakeSlotSynchronizer;


    protected SearchableMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, ContainerData inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId);
        this.inventory = inventory;
        this.access = access;
        this.inventoryData = inventoryData;
        this.syncSlots = syncSlots;
        addDataSlot(row);
        addDataSlots(inventoryData);
        addDataSlots(syncSlots);
        addPlayerInv(inventory);
    }

    public void addPlayerInv(Inventory inventory) {
        int playerX = 8;
        int playerY = 140;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + playerX, i * 18 + playerY));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, i * 18 + playerX, playerY + 58));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public int getSearchSlotCount() {
        return inventoryData.get(1);
    }

    public DataSlot getRowSlot() {
        return row;
    }
    public int getDisplaySlot(int slot) {
        return syncSlots.get(slot);
    }


    public abstract void handleInsert(ServerPlayer player, int slot);

    public void handleSearch(ServerPlayer player, String search) {
        access.execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HasSearchBar repositoryBlock) {
                repositoryBlock.setSearchString(search);
            }
        });
    }

    public void handleScroll(ServerPlayer player,int scroll_amount) {
        int rows = getSearchRows();
        if (rows > 6) {
            if (scroll_amount < 0 && row.get() < rows - 6) {
                row.set(row.get() + 1);
            } else if (scroll_amount > 0 && row.get() > 0) {
                row.set(row.get() - 1);
            }
        }
    }

    public abstract List<Integer> getDisplaySlots();

    public int getFilledSlotCount() {
        return inventoryData.get(0);
    }

    public int getCurrentRow() {
        return getRowSlot().get();
    }

    public abstract void refreshDisplay(ServerPlayer player);

    public ContainerLevelAccess getAccess() {
        return access;
    }


    public int getSearchRows() {
        return (int) Math.ceil((double) getSearchSlotCount() / 9);
    }

    //the server player calls this method once every tick
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (fakeSlotSynchronizer != null) {
            refreshDisplay(fakeSlotSynchronizer.getPlayer());
        }
    }

    public void setFakeSlotSynchronizer(FakeSlotSynchronizer fakeSlotSynchronizer) {
        this.fakeSlotSynchronizer = fakeSlotSynchronizer;
    }
}
