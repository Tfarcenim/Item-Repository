package tfar.itemrepository.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.itemrepository.ItemRepository;
import tfar.itemrepository.block.BetterBarrelBlock;
import tfar.itemrepository.init.ModBlocks;
import tfar.itemrepository.init.ModItems;
import tfar.itemrepository.item.UpgradeItem;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, ItemRepository.MODID, "en_us");
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

        addItem(() -> ModItems.STORAGE_UPGRADE,"Storage Upgrade");
        addItem(() -> ModItems.x4_STORAGE_UPGRADE,"x4 Storage Upgrade");
        addItem(() -> ModItems.x16_STORAGE_UPGRADE,"x16 Storage Upgrade");
        addItem(() -> ModItems.x64_STORAGE_UPGRADE,"x64 Storage Upgrade");

        add(BetterBarrelBlock.info,"Has %s upgrade slots");
        add(UpgradeItem.info,"Requires %s upgrade slots");
    }
}
