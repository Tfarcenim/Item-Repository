package tfar.nabba.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.nabba.NABBA;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.item.UpgradeItem;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, NABBA.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlock(() -> ModBlocks.BETTER_BARREL,"Better Barrel");
        addBlock(() -> ModBlocks.STONE_BETTER_BARREL,"Stone Better Barrel");
        addBlock(() -> ModBlocks.COPPER_BETTER_BARREL,"Copper Better Barrel");
        addBlock(() -> ModBlocks.IRON_BETTER_BARREL,"Iron Better Barrel");
        addBlock(() -> ModBlocks.LAPIS_BETTER_BARREL,"Lapis Better Barrel");
        addBlock(() -> ModBlocks.GOLD_BETTER_BARREL,"Gold Better Barrel");
        addBlock(() -> ModBlocks.DIAMOND_BETTER_BARREL,"Diamond Better Barrel");
        addBlock(() -> ModBlocks.EMERALD_BETTER_BARREL,"Emerald Better Barrel");
        addBlock(() -> ModBlocks.NETHERITE_BETTER_BARREL,"Netherite Better Barrel");
        addBlock(() -> ModBlocks.CREATIVE_BETTER_BARREL,"Creative Better Barrel");

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

        addItem(() -> ModItems.HIDE_KEY,"Hide Key");

        add(BetterBarrelBlock.info,"Has %s upgrade slots");
        add(UpgradeItem.info,"Requires %s upgrade slots");
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
    }

    protected void addDesc(Item item,String s) {
        add(item.getDescriptionId()+".desc",s);
    }
}
