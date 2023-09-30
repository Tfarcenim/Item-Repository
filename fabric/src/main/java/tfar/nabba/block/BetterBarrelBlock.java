package tfar.nabba.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tfar.nabba.api.BarrelFrameTier;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.util.BarrelType;

import javax.annotation.Nullable;

public class BetterBarrelBlock extends SingleSlotBarrelBlock {
    public BetterBarrelBlock(Properties pProperties, BarrelFrameTier barrelTier) {
        super(pProperties, BarrelType.BETTER,barrelTier);
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

                    ItemStack existing = betterBarrelBlockEntity.getItemHandler().getStack();
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(DISCRETE) ? ModBlockEntityTypes.Suppliers.DISCRETE.create(pPos,pState):ModBlockEntityTypes.Suppliers.REGULAR.create(pPos,pState);
    }
}
