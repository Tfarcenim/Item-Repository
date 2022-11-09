package tfar.nabba.menu;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.init.tag.ModBlockTags;

public class VanityKeyMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public VanityKeyMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory,ContainerLevelAccess.NULL);
    }

    public VanityKeyMenu(int pContainerId, Inventory inventory, ContainerLevelAccess access) {
            super(ModMenuTypes.VANITY_KEY, pContainerId);
        this.access = access;
        int playerX = 8;
            int playerY = 100;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + playerX, i * 18 + playerY));
                }
            }

            for (int i = 0; i < 9; i++) {
                this.addSlot(new Slot(inventory, i, i * 18 + playerX, playerY + 58));
            }
        }



    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access,pPlayer, ModBlockTags.BETTER_BARRELS);
    }

    protected static boolean stillValid(ContainerLevelAccess pAccess, Player pPlayer, TagKey<Block> targetBlocks) {
        return pAccess.evaluate((level, pos) -> level.getBlockState(pos).is(targetBlocks) && pPlayer.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
