package tfar.nabba.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;

/**
 * A wrapper around a single slot of an inventory.
 * We must ensure that only one instance of this class exists for every inventory slot,
 * or the transaction logic will not work correctly.
 * This is handled by the Map in InventoryStorageImpl.
 */
public class BetterBarrelSlotWrapper extends SingleStackStorage {
	/**
	 * The strong reference to the InventoryStorageImpl ensures that the weak value doesn't get GC'ed when individual slots are still being accessed.
	 */
	private final BetterBarrelBlockEntity.BarrelHandler barrelHandler;
	private ItemStack lastReleasedSnapshot = null;

	public BetterBarrelSlotWrapper(BetterBarrelBlockEntity.BarrelHandler storage) {
		this.barrelHandler = storage;
	}

	@Override
	protected ItemStack getStack() {
		return barrelHandler.getStack();
	}

	@Override
	protected void setStack(ItemStack stack) {
		barrelHandler.setStack(stack);
	}

	@Override
	protected boolean canInsert(ItemVariant itemVariant) {
		return barrelHandler.isItemValid(0, itemVariant.toStack());
	}

	@Override
	public int getCapacity(ItemVariant variant) {
		return barrelHandler.getSlotLimit(0);
	}

	// We override updateSnapshots to also schedule a markDirty call for the backing inventory.
	@Override
	public void updateSnapshots(TransactionContext transaction) {
		barrelHandler.markDirty();
		super.updateSnapshots(transaction);
	}

	@Override
	protected void releaseSnapshot(ItemStack snapshot) {
		lastReleasedSnapshot = snapshot;
	}

	@Override
	protected void onFinalCommit() {
		// Try to apply the change to the original stack
		ItemStack original = lastReleasedSnapshot;
		ItemStack currentStack = getStack();

		if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
			// None is empty and the items match: just update the amount and NBT, and reuse the original stack.
			original.setCount(currentStack.getCount());
			original.setTag(currentStack.hasTag() ? currentStack.getTag().copy() : null);
			setStack(original);
		} else {
			// Otherwise assume everything was taken from original so empty it.
			original.setCount(0);
		}
	}
}
