package tfar.nabba.menu;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.SearchableFluidHandler;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.FabricFluidStack;
import tfar.nabba.util.FluidMovingUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SearchableFluidMenu<T extends SearchableFluidHandler> extends SearchableMenu<FabricFluidStack> {

    public final T fluidHandler;

    protected SearchableFluidMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, T fluidHandler, ContainerData inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId, inventory, access, inventoryData, syncSlots);
        this.fluidHandler = fluidHandler;
    }

    public void refreshDisplay(ServerPlayer player, boolean forced) {
        List<FabricFluidStack> list = getDisplaySlots();

        boolean changed = forced || list.size() != remoteStacks.size();

        if (!changed) {
            for (int i = 0; i < list.size(); i++) {
                if (!remoteStacks.get(i).equals(list.get(i))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            remoteStacks.clear();
            remoteStacks.addAll(list);
            PacketHandler.sendToClient(player,PacketHandler.refresh_fluids,new FluidSync(list));
        }
    }

    public static class FluidSync implements Consumer<FriendlyByteBuf> {

        private final List<FabricFluidStack> fabricFluidStacks;

        public FluidSync(List<FabricFluidStack> list) {
            this.fabricFluidStacks = list;
        }

        @Override
        public void accept(FriendlyByteBuf buf) {
            buf.writeInt(fabricFluidStacks.size());
            IntStream.range(0,fabricFluidStacks.size()).forEach(integer -> fabricFluidStacks.get(integer).toPacket(buf));
        }
    }

    @Override
    public List<FabricFluidStack> getDisplaySlots() {
        return fluidHandler.getFluidDisplaySlots(getRowSlot().get(), getAccess().evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HasSearchBar repositoryBlock) {
                return repositoryBlock.getSearchString();
            }
            return "";
        }, ""));
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int slotIndex) {
        if (playerIn.level().isClientSide) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();


// Build the ContainerItemContext.
            ContainerItemContext handContext = ContainerItemContext.ofPlayerCursor(playerIn, this);
// Use it to query a fluid storage.
            Storage<FluidVariant> handStorage = handContext.find(FluidStorage.ITEM);

            Storage<FluidVariant> blockStorage = null;//fluidHandler.getFluidStorage();

            if (handStorage != null) {

            }
        }
        return ItemStack.EMPTY;
    }


    public T getFluidHandler() {
        return fluidHandler;
    }

    public void handleInsert(ServerPlayer player, int count) {
        ItemStack carried = getCarried();
   //     FluidActionResult result = fluidHandler.storeFluid(carried, new PlayerInvWrapper(player.getInventory()), player, false);
   //     if (result.isSuccess()) {
  //          setCarried(result.getResult());
   //     }
    }


    public void handleFluidExtract(ServerPlayer player, FabricFluidStack fluidStack, boolean shift) {

        //if (!antiBarrelInventory.isSlotValid(slot)) {
        //   return;
        //  }

        if (!shift) {
            ItemStack container = getCarried();

            //look up the wrapper for the container
            Storage<FluidVariant> handStorage = ContainerItemContext.ofPlayerCursor(player,this).find(FluidStorage.ITEM);
            if (handStorage != null) {

                Storage<FluidVariant> from = getFluidHandler().getFluidStorage();

                for (StorageView<FluidVariant> view : from) {
                    if (view.isResourceBlank()) continue;
                    FluidVariant resource = view.getResource();
                    long maxExtracted;

                    // check how much can be extracted
                    try (Transaction extractionTestTransaction = Transaction.openOuter()) {
                        maxExtracted = view.extract(resource, fluidStack.getAmount(), extractionTestTransaction);
                        extractionTestTransaction.abort();
                    }

                    try (Transaction transferTransaction = Transaction.openOuter()) {
                        // check how much can be inserted
                        long accepted = handStorage.insert(resource, maxExtracted, transferTransaction);

                        // extract it, or rollback if the amounts don't match
                        if (accepted > 0 && view.extract(resource, accepted, transferTransaction) == accepted) {
                            transferTransaction.commit();

                            //SoundEvent sound = fill ? FluidVariantAttributes.getFillSound(resource) : FluidVariantAttributes.getEmptySound(resource);

                            // Temporary workaround to use the correct sound for water bottles.
                            // TODO: Look into providing a proper item-aware fluid sound API.
                        //    if (resource.isOf(Fluids.WATER)) {
                          //      if (fill && handItem == Items.GLASS_BOTTLE) sound = SoundEvents.BOTTLE_FILL;
                          //      if (!fill && handItem == Items.POTION) sound = SoundEvents.BOTTLE_EMPTY;
                            }

                          //  player.playNotifySound(sound, SoundSource.BLOCKS, 1, 1);

                   //     }
                    }
                }


            }

        } else {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
   //             if (!stack.isEmpty()) {
    //                FluidActionResult result = fluidHandler.requestFluid(fluidStack, stack, new InvWrapper(player.getInventory()), player, false);
    //                if (result.isSuccess()) {
    //                    player.getInventory().items.set(i, result.getResult());
    //                }
    //            }
            }
        }
    }
}
