package tfar.nabba.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.capability.AntiBarrelItemStackItemHandler;
import tfar.nabba.util.NBTKeys;

public class AntiBarrelBlockItem extends BlockItem {
    public AntiBarrelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    //  @Override
//    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
  //      ItemStack disp = getStoredItem(pStack);
  //      return disp.isEmpty() ? super.getTooltipImage(pStack) : Optional.of(new BetterBarrelTooltip(disp));
   // }


    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new AntiBarrelItemStackItemHandler(stack);
    }
}
