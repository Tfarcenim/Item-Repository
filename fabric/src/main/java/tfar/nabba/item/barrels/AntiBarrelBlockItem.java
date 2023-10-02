package tfar.nabba.item.barrels;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class AntiBarrelBlockItem extends BlockItem {
    public AntiBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    //  @Override
//    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
  //      ItemStack disp = getStoredItem(stack);
  //      return disp.isEmpty() ? super.getTooltipImage(stack) : Optional.of(new BetterBarrelTooltip(disp));
   // }

}
