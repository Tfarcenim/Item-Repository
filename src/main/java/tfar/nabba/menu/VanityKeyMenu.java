package tfar.nabba.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.init.tag.ModBlockTags;

public class VanityKeyMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final Player player;
    public VanityKeyMenu(int pContainerId, Inventory inventory, BlockPos access) {
            super(ModMenuTypes.VANITY_KEY, pContainerId);
        this.pos = access;
        this.player = inventory.player;
        int playerX = 8;
            int playerY = 84;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + playerX, i * 18 + playerY));
                }
            }

            for (int i = 0; i < 9; i++) {
                this.addSlot(new Slot(inventory, i, i * 18 + playerX, playerY + 58));
            }
        }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;//stillValid(pPlayer, ModBlockTags.BETTER_BARRELS);
    }

    public void receiveVanity(int color,double size) {
            BlockEntity blockEntity= player.level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractBarrelBlockEntity betterBarrelBlockEntity) {
                betterBarrelBlockEntity.setColor(color);
                betterBarrelBlockEntity.setSize(size);
            } else if (blockEntity instanceof ControllerBlockEntity controllerBlock) {
                for (BlockPos pos1 : controllerBlock.getBarrels()) {
                    BlockEntity blockEntity1 = player.level.getBlockEntity(pos1);
                    if (blockEntity1 instanceof AbstractBarrelBlockEntity abstractBarrelBlockEntity) {
                        abstractBarrelBlockEntity.setColor(color);
                        abstractBarrelBlockEntity.setSize(size);
                    }
                }
            }
    }

    protected boolean stillValid(Player pPlayer, TagKey<Block> targetBlocks) {
        return pPlayer.level.getBlockState(pos).is(targetBlocks)
                && pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }
}
