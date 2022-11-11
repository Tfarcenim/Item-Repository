package tfar.nabba.datagen.providers.assets;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.nabba.NABBA;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.item.BarrelFrameUpgradeItem;
import tfar.nabba.item.KeyItem;
import tfar.nabba.item.UpgradeItem;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, NABBA.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //saves a couple of hundred lines of repetitive code
        for (Item item : Registry.ITEM) {
            if (item instanceof BarrelFrameUpgradeItem || item instanceof KeyItem) {
                defaultName(item);
            }
        }

        for (Block block : Registry.BLOCK) {
            if (block instanceof BetterBarrelBlock) {
                defaultName(block);
            }
        }

        addBlock(() -> ModBlocks.ANTI_BARREL,"Anti Barrel");

        addItem(() -> ModItems.STORAGE_UPGRADE,"Storage Upgrade");
        addItem(() -> ModItems.x4_STORAGE_UPGRADE,"x4 Storage Upgrade");
        addItem(() -> ModItems.x16_STORAGE_UPGRADE,"x16 Storage Upgrade");
        addItem(() -> ModItems.x64_STORAGE_UPGRADE,"x64 Storage Upgrade");
        addItem(() -> ModItems.x256_STORAGE_UPGRADE,"x256 Storage Upgrade");
        addItem(() -> ModItems.x1024_STORAGE_UPGRADE,"x1024 Storage Upgrade");
        addItem(() -> ModItems.INFINITE_STORAGE_UPGRADE,"Infinite Storage Upgrade");
        addItem(() -> ModItems.INFINITE_VENDING_UPGRADE,"Infinite Vending Upgrade");
        addItem(() -> ModItems.VOID_UPGRADE,"Void Upgrade");
        addItem(() -> ModItems.PICKUP_1x1_UPGRADE,"Pickup 1x1 Upgrade");
        addItem(() -> ModItems.PICKUP_3x3_UPGRADE,"Pickup 3x3 Upgrade");
        addItem(()-> ModItems.PICKUP_9x9_UPGRADE,"Pickup 9x9 Upgrade");
        addBlock(() -> ModBlocks.CONTROLLER,"Controller");

        add(BetterBarrelBlock.info,"Has %s upgrade slots");
        add(UpgradeItem.info,"Requires %s upgrade slots");
        add(UpgradeItem.info1,"Max of %s allowed per barrel");
        addDesc(ModItems.STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.x4_STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.x16_STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.x64_STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.x256_STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.x1024_STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.INFINITE_STORAGE_UPGRADE,"Adds %s stacks of storage");
        addDesc(ModItems.INFINITE_VENDING_UPGRADE,"Items don't deplete when extracted");
        addDesc(ModItems.VOID_UPGRADE,"Barrel voids excess items");
        addDesc(ModItems.PICKUP_3x3_UPGRADE,"Picks up items in a 3x3x3 volume centered on the barrel");
        addDesc(ModItems.PICKUP_9x9_UPGRADE,"Picks up items in a 9x3x9 area centered on the barrel");

        add("nabba.key_ring.selected_key","%s (%s)");
    }

    public void defaultName(Item item) {
        addItem(() -> item,getNameFromItem(item));
    }

    public void defaultName(Block block) {
        addBlock(() -> block,getNameFromBlock(block));
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    protected void addDesc(Item item,String s) {
        add(item.getDescriptionId()+".desc",s);
    }
}
