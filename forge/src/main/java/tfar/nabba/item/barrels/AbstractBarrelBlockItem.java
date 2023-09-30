package tfar.nabba.item.barrels;

import net.minecraft.world.item.BlockItem;
import tfar.nabba.block.AbstractBarrelBlock;

public class AbstractBarrelBlockItem<T extends AbstractBarrelBlock> extends BlockItem {
    public AbstractBarrelBlockItem(T pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
}
