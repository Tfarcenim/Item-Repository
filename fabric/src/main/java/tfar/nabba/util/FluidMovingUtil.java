package tfar.nabba.util;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

/**
 * Helper functions to work with fluid storages.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
public class FluidMovingUtil {

    /**
     * Try to make the item in a player hand "interact" with a fluid storage.
     * This can be used when a player right-clicks a tank, for example.
     *
     * <p>More specifically, this function tries to find a fluid storing item in the player's hand.
     * Then, it tries to fill that item from the storage. If that fails, it tries to fill the storage from that item.
     *
     * <p>Only up to one fluid variant will be moved, and the corresponding emptying/filling sound will be played.
     * In creative mode, the original container item is not modified,
     * and the player's inventory will additionally receive a copy of the modified container, if it doesn't have it yet.
     *
     * @param storage The storage that the player is interacting with.
     * @param player  The player.
     * @param hand    The hand that the player used.
     * @return True if some fluid was moved.
     */
    public static boolean interactWithFluidStorageEmpty(Storage<FluidVariant> storage, Player player, InteractionHand hand) {
        // Check if hand is a fluid container.
        Storage<FluidVariant> handStorage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (handStorage == null) return false;

        // Try to fill hand first, otherwise try to empty it.
        Item handItem = player.getItemInHand(hand).getItem();

        try {
            return moveWithSound(storage, handStorage, player, true, handItem);
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Interacting with fluid storage");
            report.addCategory("Interaction details")
                    .setDetail("Player", () -> DebugMessages.forPlayer(player))
                    .setDetail("Hand", hand)
                    .setDetail("Hand item", handItem::toString)
                    .setDetail("Fluid storage", () -> Objects.toString(storage, null));
            throw new ReportedException(report);
        }
    }

    /**
     * attempt to add fluid to a storage
     */
    public static boolean interactWithFluidStorageFill(Storage<FluidVariant> storage, FabricFluidStack fabricFluidStack) {
            try (Transaction transferTransaction = Transaction.openOuter()) {
                // check how much can be inserted
                // extract it, or rollback if the amounts don't match
                storage.insert(fabricFluidStack.getFluidVariant(), fabricFluidStack.getAmount(), transferTransaction);
                transferTransaction.commit();

                return true;
            } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Interacting with fluid storage");
            report.addCategory("Interaction details")
                    .setDetail("Fluid storage", () -> Objects.toString(storage, null));
            throw new ReportedException(report);
        }
    }


    /**
     * Try to make the item in a player hand "interact" with a fluid storage.
     * This can be used when a player right-clicks a tank, for example.
     *
     * <p>More specifically, this function tries to find a fluid storing item in the player's hand.
     * Then, it tries to fill that item from the storage. If that fails, it tries to fill the storage from that item.
     *
     * <p>Only up to one fluid variant will be moved, and the corresponding emptying/filling sound will be played.
     * In creative mode, the original container item is not modified,
     * and the player's inventory will additionally receive a copy of the modified container, if it doesn't have it yet.
     *
     * @param storage The storage that the player is interacting with.
     * @param player  The player.
     * @param hand    The hand that the player used.
     * @return True if some fluid was moved.
     */
    public static boolean interactWithFluidStorageFill(Storage<FluidVariant> storage, Player player, InteractionHand hand) {
        // Check if hand is a fluid container.
        Storage<FluidVariant> handStorage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (handStorage == null) return false;

        // Try to fill hand first, otherwise try to empty it.
        Item handItem = player.getItemInHand(hand).getItem();

        try {
            return moveWithSound(handStorage, storage, player, false, handItem);
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Interacting with fluid storage");
            report.addCategory("Interaction details")
                    .setDetail("Player", () -> DebugMessages.forPlayer(player))
                    .setDetail("Hand", hand)
                    .setDetail("Hand item", handItem::toString)
                    .setDetail("Fluid storage", () -> Objects.toString(storage, null));
            throw new ReportedException(report);
        }
    }

    private static boolean moveWithSound(Storage<FluidVariant> from, Storage<FluidVariant> to, Player player, boolean fill, Item handItem) {
        for (StorageView<FluidVariant> view : from) {
            if (view.isResourceBlank()) continue;
            FluidVariant resource = view.getResource();
            long maxExtracted;

            // check how much can be extracted
            try (Transaction extractionTestTransaction = Transaction.openOuter()) {
                maxExtracted = view.extract(resource, Long.MAX_VALUE, extractionTestTransaction);
                extractionTestTransaction.abort();
            }

            try (Transaction transferTransaction = Transaction.openOuter()) {
                // check how much can be inserted
                long accepted = to.insert(resource, maxExtracted, transferTransaction);

                // extract it, or rollback if the amounts don't match
                if (accepted > 0 && view.extract(resource, accepted, transferTransaction) == accepted) {
                    transferTransaction.commit();

                    SoundEvent sound = fill ? FluidVariantAttributes.getFillSound(resource) : FluidVariantAttributes.getEmptySound(resource);

                    // Temporary workaround to use the correct sound for water bottles.
                    // TODO: Look into providing a proper item-aware fluid sound API.
                    if (resource.isOf(Fluids.WATER)) {
                        if (fill && handItem == Items.GLASS_BOTTLE) sound = SoundEvents.BOTTLE_FILL;
                        if (!fill && handItem == Items.POTION) sound = SoundEvents.BOTTLE_EMPTY;
                    }

                    player.playNotifySound(sound, SoundSource.BLOCKS, 1, 1);

                    return true;
                }
            }
        }
        return false;
    }
}
