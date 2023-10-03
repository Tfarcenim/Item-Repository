package tfar.nabba.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.CommonClientUtils;
import tfar.nabba.client.StackSizeRenderer;
import tfar.nabba.client.gui.RightClickButton;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.net.PacketHandler;
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

        int count = Math.min(stack.getMaxStackSize(),stack.getCount());

        if (mouseStack.isEmpty() &&!stack.isEmpty()) {//try to take item
            PacketHandler.sendToServer(PacketHandler.extract_item,buf -> {
                buf.writeItem(CommonUtils.copyStackWithSize(stack,count));
                buf.writeBoolean(shift);
            });
        } else if (!mouseStack.isEmpty()) {//try to insert full stack
            PacketHandler.sendToServer(PacketHandler.insert,buf -> buf.writeInt(mouseStack.getCount()));
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRightClick(double pMouseX, double pMouseY) {

        ItemStack mouseStack = screen.getMenu().getCarried();

        if (!mouseStack.isEmpty() && stack.isEmpty()) {//try to add one item
            PacketHandler.sendToServer(PacketHandler.insert,buf -> buf.writeInt(1));
        } else if (mouseStack.isEmpty()) {
            //take half
            int count = (int) Math.ceil(Math.min(stack.getMaxStackSize(),stack.getCount()) / 2d);
            PacketHandler.sendToServer(PacketHandler.extract_item,buf -> {
                buf.writeItem(CommonUtils.copyStackWithSize(stack,count));
                buf.writeBoolean(false);
            });
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

        String amount = (stack.getCount() > 1) ? CommonUtils.formatLargeNumber(stack.getCount()) : "";
        StackSizeRenderer.renderSizeLabel(matrices,Minecraft.getInstance().font, getX(),getY(),amount);
        //CommonClientUtils.drawSmallItemNumbers(matrices,getX(),getY(),stack);
        matrices.renderItemDecorations(Minecraft.getInstance().font,stack, getX(), getY(),"");
        matrices.renderFakeItem(stack,getX(),getY());
    }

    public void renderTooltip(GuiGraphics matrices,int x,int y) {
        screen.renderItemTooltip(matrices,stack,x,y);
    }
}