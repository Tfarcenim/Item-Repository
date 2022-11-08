package tfar.nabba.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import tfar.nabba.NABBA;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.util.BarrelTier;

import javax.annotation.Nullable;
import java.util.List;

public class BetterBarrelBlock extends Block implements EntityBlock {
    private final BarrelTier barrelTier;
    public static final BooleanProperty VOID = BooleanProperty.create("void");
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public BetterBarrelBlock(Properties pProperties, BarrelTier barrelTier) {
        super(pProperties);
        registerDefaultState(this.stateDefinition.any().setValue(VOID,false).setValue(LOCKED,false));
        this.barrelTier = barrelTier;
    }

    public BarrelTier getBarrelTier() {
        return barrelTier;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack handStack = pPlayer.getItemInHand(pHand);

        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
                Item item = handStack.getItem();
                if (item instanceof UpgradeItem upgradeItem && tryUpgrade(handStack,betterBarrelBlockEntity,upgradeItem)) {

                } else {

                    ItemStack existing = betterBarrelBlockEntity.getBarrelHandler().getStack();
                    //there is no items in the barrel OR the item that the player is holding is the same as the item in the barrel
                    if (existing.isEmpty() || ItemStack.isSameItemSameTags(handStack,existing)) {
                        ItemStack stack = betterBarrelBlockEntity.tryAddItem(handStack);
                        pPlayer.setItemInHand(pHand, stack);
                    } else {
                        //search the entire inventory for item stacks
                        Inventory inventory = pPlayer.getInventory();
                        NonNullList<ItemStack> main = inventory.items;
                        for (int i = 0; i < main.size();i++) {
                            ItemStack fromPlayer = main.get(i);
                            if (!fromPlayer.isEmpty()) {
                                ItemStack insert = betterBarrelBlockEntity.tryAddItem(fromPlayer);
                                //if the item changed, something happened
                                if (insert != fromPlayer) {
                                    main.set(i, insert);
                                }
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
                ItemStack stack = betterBarrelBlockEntity.tryRemoveItem();
                ItemHandlerHelper.giveItemToPlayer(pPlayer,stack);
            }
        }
    }

    public static final String info = NABBA.MODID+".better_barrel.info";
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable(info,Component.translatable(barrelTier.getUpgradeSlots()+"").withStyle(ChatFormatting.AQUA)));
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public boolean tryUpgrade(ItemStack itemstack, BetterBarrelBlockEntity betterBarrelBlockEntity, UpgradeItem item) {
        boolean attempt = betterBarrelBlockEntity.canAcceptUpgrade(item.getData());
        if (attempt) {
            betterBarrelBlockEntity.upgrade(item.getData());
            itemstack.shrink(1);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntityTypes.Suppliers.WOOD.create(pPos, pState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : BetterBarrelBlockEntity::serverTick;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VOID,LOCKED);
    }
}
