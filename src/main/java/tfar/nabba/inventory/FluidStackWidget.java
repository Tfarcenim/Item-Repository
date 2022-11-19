package tfar.nabba.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.net.C2SExtractFluidPacket;
import tfar.nabba.net.C2SInsertPacket;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.ClientUtils;

public class FluidStackWidget extends RightClickButton<FluidStack> {

    public FluidStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableFluidScreen<?,?> screen, int index) {
        super(pX, pY, pWidth, pHeight, pMessage, screen, index);
        stack = FluidStack.EMPTY;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        final boolean shift = Screen.hasShiftDown();

        ItemStack carried = screen.getMenu().getCarried();

        if (carried.isEmpty()&& shift) {
            //shiftclicking on a slot should try to extract to fluid containers in inventory
            PacketHandler.sendToServer(new C2SExtractFluidPacket(index,shift));
        } else {
            carried.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandlerItem -> {
                boolean emptyContainer = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).isEmpty();
                if (emptyContainer && !stack.isEmpty()) {//try to take fluid
                    PacketHandler.sendToServer(new C2SExtractFluidPacket(index, shift));

                } else {//try to insert fluid
                    PacketHandler.sendToServer(new C2SInsertPacket(index));
                }

            });
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRightClick(double pMouseX, double pMouseY) {

    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            renderFluid(pPoseStack);
            if (isHovered) {
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    public void renderFluid(PoseStack matrices) {
        ClientUtils.renderFluid(matrices,x,y,stack);
    }


    public void renderTooltip(PoseStack matrices,int x,int y) {
        screen.renderTooltip(matrices,stack.getDisplayName(),x,y);
    }
}