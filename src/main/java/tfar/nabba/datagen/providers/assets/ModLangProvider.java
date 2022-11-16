package tfar.nabba.datagen.providers.assets;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.nabba.NABBA;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.item.BarrelFrameUpgradeItem;
import tfar.nabba.item.keys.KeyItem;
import tfar.nabba.item.StorageUpgradeItem;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.Utils;

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
            } else if (item instanceof StorageUpgradeItem storageUpgradeItem) {
                storageUpgradeName(item);
                addTooltip(item,"Adds %s "+storageUpgradeItem.getType().s+" of storage");
            }
        }

        for (Block block : Registry.BLOCK) {
            if (block instanceof AbstractBarrelBlock) {
                defaultName(block);
            }
        }


        addItem(() -> ModItems.INFINITE_VENDING_UPGRADE,"Infinite Vending Upgrade");
        addItem(() -> ModItems.VOID_UPGRADE,"Void Upgrade");
        addItem(() -> ModItems.PICKUP_1x1_UPGRADE,"Pickup 1x1 Upgrade");
        addItem(() -> ModItems.PICKUP_3x3_UPGRADE,"Pickup 3x3 Upgrade");
        addItem(()-> ModItems.PICKUP_9x9_UPGRADE,"Pickup 9x9 Upgrade");
        addBlock(() -> ModBlocks.CONTROLLER,"Controller");

        add(AbstractBarrelBlock.info,"Using %s upgrade slots");
        add(UpgradeItem.info,"Requires %s upgrade slots");
        add(UpgradeItem.info1,"Max of %s allowed per barrel");

        addTooltip(ModItems.INFINITE_VENDING_UPGRADE,"Items don't deplete when extracted");
        addTooltip(ModItems.VOID_UPGRADE,"Barrel voids excess items");
        addTooltip(ModItems.PICKUP_1x1_UPGRADE,"Picks up items in a 1x3x1 volume centered on the barrel");
        addTooltip(ModItems.PICKUP_3x3_UPGRADE,"Picks up items in a 3x3x3 volume centered on the barrel");
        addTooltip(ModItems.PICKUP_9x9_UPGRADE,"Picks up items in a 9x3x9 area centered on the barrel");
        addTooltip(ModBlocks.CONTROLLER, "Connects to all barrels and tanks from this mod within a "
                +(2 * Utils.RADIUS+1)+"x"+(2 * Utils.RADIUS+1)+"x"+(2 * Utils.RADIUS+1)+" volume");

        add("nabba.key_ring.selected_key","%s (%s)");
    }

    public void storageUpgradeName(Item item) {
        addItem(()-> item,getNameFromItem(item).replace("X","x"));
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

    protected void addTooltip(Block item, String s) {
        add(item.getDescriptionId()+".tooltip",s);
    }
    protected void addTooltip(Item item, String s) {
        add(item.getDescriptionId()+".tooltip",s);
    }
}
