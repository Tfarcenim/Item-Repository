package tfar.nabba.client.screen.button;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.gui.RightClickButton;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.ClientUtils;
import tfar.nabba.util.FabricFluidStack;

public class FluidStackWidget extends RightClickButton<FabricFluidStack,SearchableFluidScreen<?,?>> {

    public FluidStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableFluidScreen<?,?> screen) {
        super(pX, pY, pWidth, pHeight, pMessage, screen);
        stack = FabricFluidStack.empty();
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        final boolean shift = Screen.hasShiftDown();

        ItemStack carried = screen.getMenu().getCarried();

        if (shift) {
            //shiftclicking on a slot should try to extract to fluid containers in inventory
            PacketHandler.sendToServer(PacketHandler.extract_fluid,buf -> {
              stack.toPacket(buf);
              buf.writeBoolean(shift);
            });
        } else {

            //look up the wrapper for the container
            Storage<FluidVariant> handStorage = ContainerItemContext.ofPlayerCursor(Minecraft.getInstance().player,screen.getMenu()).find(FluidStorage.ITEM);

            if (handStorage != null) {
                ResourceAmount<FluidVariant> extractableContent = StorageUtil.findExtractableContent(handStorage, null);

                //there is fluid in held container, try to store it
                if (extractableContent != null && extractableContent.amount() > 0) {
                    PacketHandler.sendToServer(PacketHandler.insert,buf -> {
                        buf.writeLong(Long.MAX_VALUE);
                    });
                } else {
                    //try to take fluid from stack
                    PacketHandler.sendToServer(PacketHandler.extract_fluid,buf -> {
                        stack.toPacket(buf);
                        buf.writeBoolean(false);
                    });
                }
            }
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRightClick(double pMouseX, double pMouseY) {

    }

    @Override
    public void renderWidget(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            renderFluid(pPoseStack);
            if (isHovered) {
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    public void renderFluid(GuiGraphics matrices) {
        ClientUtils.renderFluidinSlot(matrices, getX(), getY(),stack);
    }


    public void renderTooltip(GuiGraphics matrices,int x,int y) {
        screen.renderFluidTooltip(matrices,stack,x,y);
    }
}