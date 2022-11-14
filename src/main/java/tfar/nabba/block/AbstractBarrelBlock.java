package tfar.nabba.block;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.NABBA;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.item.BetterBarrelBlockItem;
import tfar.nabba.util.BarrelType;

import java.util.List;

public abstract class AbstractBarrelBlock extends Block implements EntityBlock {

    public static final String info = NABBA.MODID+".barrel.info";
    protected final BarrelFrameTier barrelTier;
    public static final BooleanProperty VOID = BooleanProperty.create("void");
    public static final BooleanProperty DISCRETE = BooleanProperty.create("discrete");
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    private final BarrelType type;

    public AbstractBarrelBlock(Properties pProperties, BarrelType type,BarrelFrameTier tier) {
        super(pProperties);
        this.type = type;
        this.barrelTier = tier;
        registerDefaultState(this.stateDefinition.any().setValue(VOID,false));
    }

    public BarrelFrameTier getBarrelTier() {
        return barrelTier;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        appendBlockStateInfo(pStack,pTooltip);
        pTooltip.add(Component.translatable(info,
                Component.translatable(BetterBarrelBlockItem.getUsedSlotsFromItem(pStack)+"/"+barrelTier.getUpgradeSlots()).withStyle(ChatFormatting.AQUA)));
        if (pStack.hasTag())
        pTooltip.add(Component.literal(pStack.getTag().toString()).withStyle(ChatFormatting.YELLOW));
    }

    public void appendBlockStateInfo(ItemStack stack, List<Component> tooltip) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag().getCompound("BlockStateTag");
            if (!tag.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.literal("Discrete: ").append(Component.literal(tag.getString(DISCRETE.getName())).withStyle(ChatFormatting.YELLOW)));
                tooltip.add(Component.literal("Locked: ").append(Component.literal(tag.getString(LOCKED.getName())).withStyle(ChatFormatting.YELLOW)));
                tooltip.add(Component.literal("Void: ").append(Component.literal(tag.getString(VOID.getName())).withStyle(ChatFormatting.YELLOW)));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VOID);
    }

    public BarrelType getType() {
        return type;
    }
}
