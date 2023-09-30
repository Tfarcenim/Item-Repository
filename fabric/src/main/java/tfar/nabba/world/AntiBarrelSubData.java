package tfar.nabba.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;

import java.io.File;

public class AntiBarrelSubData extends SavedData {

    ListTag storage = new ListTag();

    public AntiBarrelSubData() {
    }

    public ListTag getStorage() {
        return storage;
    }

    public void saveData(ListTag tag) {
        storage = tag;
        setDirty();
    }

    public AntiBarrelBlockEntity.AntiBarrelInventory getInventory(AntiBarrelBlockEntity antiBarrelBlockEntity) {
        return createFromTag(antiBarrelBlockEntity, storage);
    }

    public AntiBarrelBlockEntity.AntiBarrelInventory createFromTag(AntiBarrelBlockEntity antiBarrelBlockEntity, ListTag tag) {
        AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory = new AntiBarrelBlockEntity.AntiBarrelInventory(antiBarrelBlockEntity);
        antiBarrelInventory.loadItems(tag);
        return antiBarrelInventory;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("contents", storage);
        return compoundTag;
    }

    public static AntiBarrelSubData loadStatic(CompoundTag compoundTag) {
        AntiBarrelSubData antiBarrelSavedData = new AntiBarrelSubData();
        antiBarrelSavedData.load(compoundTag);
        return antiBarrelSavedData;
    }

    protected void load(CompoundTag compoundTag) {
        storage = compoundTag.getList("contents", Tag.TAG_COMPOUND);
    }

    public void writeInvData(AntiBarrelBlockEntity.AntiBarrelInventory antiBarrelInventory) {
        storage = antiBarrelInventory.save();
        setDirty();
    }

    @Override
    public void save(File file) {
        super.save(file);
    }

    public boolean clear() {
        storage = new ListTag();
        setDirty();
        return true;
    }
}
