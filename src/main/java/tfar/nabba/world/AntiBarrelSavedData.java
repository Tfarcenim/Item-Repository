package tfar.nabba.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;

import java.io.File;
import java.util.*;

public class AntiBarrelSavedData extends SavedData {

    private final Map<UUID,ListTag> storage = new HashMap<>();

    public AntiBarrelSavedData() {
    }

    public Map<UUID,ListTag> getStorage() {
        return storage;
    }

    public AntiBarrelBlockEntity.AntiBarrelInventory getInventory(AntiBarrelBlockEntity antiBarrelBlockEntity) {
        UUID uuid = antiBarrelBlockEntity.getUuid();
        if (!storage.containsKey(uuid)) {
            storage.put(uuid,new ListTag());
        }
        ListTag tag = storage.get(uuid);
        return createFromTag(antiBarrelBlockEntity,tag);
    }

    public AntiBarrelBlockEntity.AntiBarrelInventory createFromTag(AntiBarrelBlockEntity antiBarrelBlockEntity, ListTag tag) {
        AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory = new AntiBarrelBlockEntity.AntiBarrelInventory(antiBarrelBlockEntity);
        antiBarrelInventory.loadItems(tag);
        return antiBarrelInventory;
    }


    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID,ListTag> inventory : storage.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("content",inventory.getValue());
            tag.putUUID("uuid",inventory.getKey());
            list.add(tag);
        }
        compoundTag.put("contents", list);
        return compoundTag;
    }

    public static AntiBarrelSavedData loadStatic(CompoundTag compoundTag) {
        AntiBarrelSavedData antiBarrelSavedData = new AntiBarrelSavedData();
        antiBarrelSavedData.load(compoundTag);
        return antiBarrelSavedData;
    }

    protected void load(CompoundTag compoundTag) {
        ListTag invs = compoundTag.getList("contents", Tag.TAG_COMPOUND);
        for (Tag tag : invs) {
            CompoundTag compoundTag1 = (CompoundTag)tag;
            storage.put(compoundTag1.getUUID("uuid"),compoundTag1.getList("content",Tag.TAG_COMPOUND));
        }
    }

    public void writeInvData(AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory) {
        storage.put(antiBarrelInventory.getBlockEntity().getUuid(), antiBarrelInventory.save());
        setDirty();
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

    public boolean clearId(UUID id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
            setDirty();
            return true;
        }
        return false;
    }

}
