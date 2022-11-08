package tfar.nabba.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.nabba.util.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RepositorySavedData extends SavedData {

    private final List<RepositoryInventory> storage = new ArrayList<>();

    public RepositorySavedData() {
    }

    @Nullable
    public RepositoryInventory getInventory(int id) {
        return (id < storage.size() && id > Utils.INVALID) ? storage.get(id) : null;
    }

    public void createInventory() {
        int next = getNextID();
        RepositoryInventory inventory = new RepositoryInventory();
        storage.add(next, inventory);
        setDirty();
    }

    public RepositoryInventory getOrCreateInventory(int id) {
        RepositoryInventory repositoryInventory = getInventory(id);
        if (repositoryInventory == null) {
            createInventory();
        }
        return getInventory(id);
    }

    public int getNextID() {
        return storage.size();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        for (RepositoryInventory inventory : storage) {
            listTag.add(inventory.save());
        }
        compoundTag.put("contents", listTag);
        return compoundTag;
    }

    public static RepositorySavedData loadStatic(CompoundTag compoundTag) {
        RepositorySavedData repositorySavedData = new RepositorySavedData();
        repositorySavedData.load(compoundTag);
        return repositorySavedData;
    }

    protected void load(CompoundTag compoundTag) {
        ListTag invs = compoundTag.getList("contents", Tag.TAG_LIST);
        for (Tag tag : invs) {
            ListTag compoundTag1 = (ListTag) tag;
            RepositoryInventory repositoryInventory = readItems(compoundTag1);
            storage.add(repositoryInventory);
        }
    }

    RepositoryInventory readItems(ListTag tag) {
        RepositoryInventory inventory = new RepositoryInventory();
        inventory.loadItems(tag);
        return inventory;
    }

    @Override
    public void save(File file) {
        super.save(file);
        //DankStorage.LOGGER.debug("Saving Dank Contents");
    }

    public void clearAll() {
        storage.clear();
        setDirty();
    }

    public boolean clearId(int id) {
        if (id < getNextID()) {
            RepositoryInventory repositoryInventory = getInventory(id);
            //repositoryInventory.clearContent();
            setDirty();
            return true;
        }
        return false;
    }

}
