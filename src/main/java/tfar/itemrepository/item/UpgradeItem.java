package tfar.itemrepository.item;

import net.minecraft.world.item.Item;
import tfar.itemrepository.util.UpgradeData;

public class UpgradeItem extends Item {
    private final UpgradeData data;

    public UpgradeItem(Properties pProperties, UpgradeData data) {
        super(pProperties);
        this.data = data;
    }

    public UpgradeData getData() {
        return data;
    }
}
