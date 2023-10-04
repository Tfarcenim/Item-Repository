package tfar.nabba.item.barrels;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import tfar.nabba.util.FabricFluidStack;

public abstract class SingleFluidStackStorage  extends SnapshotParticipant<FabricFluidStack> implements SingleSlotStorage<FluidVariant> {
        /**
         * Return the stack of this storage. It will be modified directly sometimes to avoid needless copies.
         * However, any mutation of the stack will directly be followed by a call to {@link #setStack}.
         * This means that either returning the backing stack directly or a copy is safe.
         *
         * @return The current stack.
         */
        protected abstract FabricFluidStack getStack();

        /**
         * Set the stack of this storage.
         */
        protected abstract void setStack(FabricFluidStack stack);

        /**
         * Return {@code true} if the passed non-blank item variant can be inserted, {@code false} otherwise.
         */
        protected boolean canInsert(FluidVariant FluidVariant) {
            return true;
        }

        /**
         * Return {@code true} if the passed non-blank item variant can be extracted, {@code false} otherwise.
         */
        protected boolean canExtract(FluidVariant FluidVariant) {
            return true;
        }

        @Override
        public boolean isResourceBlank() {
            return getStack().isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            return getStack().getFluidVariant();
        }

        @Override
        public long getAmount() {
            return getStack().getAmount();
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);

            FabricFluidStack currentStack = getStack();

            if ((insertedVariant.equals(currentStack.getFluidVariant()) || currentStack.isEmpty()) && canInsert(insertedVariant)) {
                int insertedAmount = (int) Math.min(maxAmount, getCapacity() - currentStack.getAmount());

                if (insertedAmount > 0) {
                    updateSnapshots(transaction);
                    currentStack = getStack();

                    if (currentStack.isEmpty()) {
                        currentStack = new FabricFluidStack(insertedVariant,insertedAmount);
                    } else {
                        currentStack.grow(insertedAmount);
                    }

                    setStack(currentStack);

                    return insertedAmount;
                }
            }

            return 0;
        }

        @Override
        public long extract(FluidVariant variant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(variant, maxAmount);

            FabricFluidStack currentStack = getStack();

            if (variant.equals(currentStack.getFluidVariant()) && canExtract(variant)) {
                int extracted = (int) Math.min(currentStack.getAmount(), maxAmount);

                if (extracted > 0) {
                    this.updateSnapshots(transaction);
                    currentStack = getStack();
                    currentStack.shrink(extracted);
                    setStack(currentStack);

                    return extracted;
                }
            }

            return 0;
        }

        @Override
        protected FabricFluidStack createSnapshot() {
            FabricFluidStack original = getStack();
            setStack(original.copy());
            return original;
        }

        @Override
        protected void readSnapshot(FabricFluidStack snapshot) {
            setStack(snapshot);
        }

        @Override
        public String toString() {
            return "SingleFluidStackStorage[" + getStack() + "]";
        }
    }
