package tfar.nabba.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.Nullable;
import tfar.nabba.api.HasSearchBar;
import tfar.nabba.api.SearchableFluidHandler;
import tfar.nabba.net.PacketHandler;

import java.util.List;

public class SearchableFluidMenu<T extends SearchableFluidHandler> extends SearchableMenu<FluidStack> {

    public final T fluidHandler;

    protected SearchableFluidMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ContainerLevelAccess access, T fluidHandler, ContainerData inventoryData, ContainerData syncSlots) {
        super(pMenuType, pContainerId, inventory, access, inventoryData, syncSlots);
        this.fluidHandler = fluidHandler;
    }

    public void refreshDisplay(ServerPlayer player, boolean forced) {
        List<FluidStack> list = getDisplaySlots();

        boolean changed = forced || list.size() != remoteStacks.size();

        if (!changed) {
            for (int i = 0; i < list.size(); i++) {
                if (!remoteStacks.get(i).isFluidStackIdentical(list.get(i))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            remoteStacks.clear();
            remoteStacks.addAll(list);
            PacketHandler.sendToClient(new S2CRefreshClientFluidStacksPacket(list), player);
        }
    }

    @Override
    public List<FluidStack> getDisplaySlots() {
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

            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(iFluidHandlerItem -> {
                if (!iFluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
                    FluidActionResult result = fluidHandler.storeFluid(stack, new PlayerInvWrapper(playerIn.getInventory()), (ServerPlayer) playerIn, false);
                    if (result.isSuccess()) {
                        slot.set(result.getResult());
                        slot.onTake(playerIn, stack);
                    }
                }
            });
        }
        return ItemStack.EMPTY;
    }


    public T getFluidHandler() {
        return fluidHandler;
    }

    public void handleInsert(ServerPlayer player, int count) {
        ItemStack carried = getCarried();
        FluidActionResult result = fluidHandler.storeFluid(carried, new PlayerInvWrapper(player.getInventory()), player, false);
        if (result.isSuccess()) {
            setCarried(result.getResult());
        }
    }


    public void handleFluidExtract(ServerPlayer player, FluidStack fluidStack, boolean shift) {

        //if (!antiBarrelInventory.isSlotValid(slot)) {
        //   return;
        //  }

        if (!shift) {
            ItemStack container = getCarried();
            FluidActionResult result = fluidHandler.requestFluid(fluidStack, container, new InvWrapper(player.getInventory()), player, false);

            if (result.isSuccess()) {
                setCarried(result.getResult());
            }

        } else {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
                if (!stack.isEmpty()) {
                    FluidActionResult result = fluidHandler.requestFluid(fluidStack, stack, new InvWrapper(player.getInventory()), player, false);
                    if (result.isSuccess()) {
                        player.getInventory().items.set(i, result.getResult());
                    }
                }
            }
        }
    }
}
