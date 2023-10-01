package tfar.nabba.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.ClientUtils;
import tfar.nabba.util.CommonUtils;

public class ItemStackWidget extends RightClickButton<ItemStack,SearchableItemScreen<?,?>> {

    private final SearchableItemScreen<?,?> screen;

    public ItemStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableItemScreen<?,?> screen) {
        super(pX, pY, pWidth, pHeight, pMessage, screen);
        this.screen = screen;
        stack = ItemStack.EMPTY;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        boolean shift = Screen.hasShiftDown();

        ItemStack mouseStack = screen.getMenu().getCarried();

        if (mouseStack.isEmpty() &&!stack.isEmpty()) {//try to take item
            PacketHandler.sendToServer(new C2SExtractItemPacket(stack, shift));
        } else if (!mouseStack.isEmpty()) {//try to insert full stack
            PacketHandler.sendToServer(new C2SInsertPacket(mouseStack.getCount()));
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRightClick(double pMouseX, double pMouseY) {

        ItemStack mouseStack = screen.getMenu().getCarried();

        if (!mouseStack.isEmpty() && stack.isEmpty()) {//try to add one item
            PacketHandler.sendToServer(new C2SInsertPacket(1));
        } else if (mouseStack.isEmpty()) {
            //take half
            int count = (int) Math.ceil(Math.min(stack.getMaxStackSize(),stack.getCount()) / 2d);
            PacketHandler.sendToServer(new C2SExtractItemPacket(CommonUtils.copyStackWithSize(stack,count), false));
        }

        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void renderWidget(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            if (isHovered) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                pPoseStack.fill(getX(), getY(), getX() + 16, getY() + 16, 0x80ffffff);
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
            renderItem(pPoseStack);
        }
    }

    public void renderItem(GuiGraphics matrices) {
        ClientUtils.drawSmallItemNumbers(matrices,getX(),getY(),stack);
        matrices.renderItemDecorations(Minecraft.getInstance().font,stack, getX(), getY());
    }

    public void renderTooltip(GuiGraphics matrices,int x,int y) {
        screen.renderItemTooltip(matrices,stack,x,y);
    }
}