package tfar.itemrepository.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tfar.itemrepository.blockentity.BetterBarrelBlockEntity;
import tfar.itemrepository.init.ModBlockEntityTypes;
import tfar.itemrepository.item.UpgradeItem;
import tfar.itemrepository.util.BarrelTier;

import javax.annotation.Nullable;

public class BetterBarrelBlock extends Block implements EntityBlock {
    private final BarrelTier barrelTier;

    public BetterBarrelBlock(Properties pProperties, BarrelTier barrelTier) {
        super(pProperties);
        this.barrelTier = barrelTier;
    }

    public BarrelTier getBarrelTier() {
        return barrelTier;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
                BetterBarrelBlockEntity.BarrelHandler barrelHandler = betterBarrelBlockEntity.getBarrelHandler();
                Item item = itemstack.getItem();
                if (item instanceof UpgradeItem upgradeItem) {
                    tryUpgrade(upgradeItem);
                } else {
                    ItemStack stack = betterBarrelBlockEntity.tryAddItem(itemstack);
                    pPlayer.setItemInHand(pHand,stack);
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public boolean tryUpgrade(UpgradeItem item) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntityTypes.Suppliers.WOOD.create(pPos, pState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (BetterBarrelBlockEntity::serverTick);
    }
}
