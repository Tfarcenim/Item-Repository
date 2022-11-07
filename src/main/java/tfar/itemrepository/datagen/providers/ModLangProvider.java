package tfar.itemrepository.datagen.providers;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.itemrepository.ItemRepository;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, ItemRepository.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

    }
}
