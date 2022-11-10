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
import net.minecraft.world.item.context.BlockPlaceContext;
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
import tfar.nabba.item.InteractsWithBarrel;
import tfar.nabba.item.KeyItem;
import tfar.nabba.item.UpgradeItem;
import tfar.nabba.api.BarrelFrameTier;

import javax.annotation.Nullable;
import java.util.List;

public class BetterBarrelBlock extends Block implements EntityBlock {
    private final BarrelFrameTier barrelTier;
    public static final BooleanProperty VOID = BooleanProperty.create("void");
    public static final BooleanProperty DISCRETE = BooleanProperty.create("discrete");
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public BetterBarrelBlock(Properties pProperties, BarrelFrameTier barrelTier) {
        super(pProperties);
        registerDefaultState(this.stateDefinition.any().setValue(VOID,false).setValue(LOCKED,false).setValue(DISCRETE,false));
        this.barrelTier = barrelTier;
    }

    public BarrelFrameTier getBarrelTier() {
        return barrelTier;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack handStack = pPlayer.getItemInHand(pHand);

        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
                Item item = handStack.getItem();

                //remember, this gets called before the item's onUse method
                if (item instanceof InteractsWithBarrel interactsWithBarrel && interactsWithBarrel.handleBarrel(pState,handStack,pLevel,pPos,pPlayer)) {

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntityTypes.Suppliers.WOOD.create(pPos,pState);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand();

        return super.getStateForPlacement(pContext);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (Level pLevel1, BlockPos pPos, BlockState pState1, T pBlockEntity) -> BetterBarrelBlockEntity.serverTick(pLevel1, pPos, pState1, (BetterBarrelBlockEntity) pBlockEntity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VOID,LOCKED,DISCRETE);
    }
}
