package tfar.nabba.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.inventory.RightClickButton;
import tfar.nabba.net.server.C2SInsertPacket;
import tfar.nabba.net.server.C2SExtractItemPacket;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.ClientUtils;
import tfar.nabba.util.Utils;

public class ItemStackWidget extends RightClickButton<ItemStack> {

    private final SearchableItemScreen<?,?> screen;

    public ItemStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableItemScreen<?,?> screen) {
        super(pX, pY, pWidth, pHeight, pMessage, screen);
        this.screen = screen;
        stack = ItemStack.EMPTY;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        boolean shift = Screen.hasShiftDown();

        if (screen.getMenu().getCarried().isEmpty() &&!stack.isEmpty()) {//try to take item
            PacketHandler.sendToServer(new C2SExtractItemPacket(stack, shift));
        } else if (!screen.getMenu().getCarried().isEmpty()){//try to insert item
            PacketHandler.sendToServer(new C2SInsertPacket());
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRightClick(double pMouseX, double pMouseY) {
        if (!screen.getMenu().getCarried().isEmpty() && stack.isEmpty()) {//try to add item
            PacketHandler.sendToServer(new C2SInsertPacket());
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            if (isHovered) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                fill(pPoseStack, getX(), getY(), getX() + 16, getY() + 16, 0x80ffffff);
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
            renderItem(pPoseStack);
        }
    }

    public void renderItem(PoseStack matrices) {
        ClientUtils.drawSmallItemNumbers(matrices,getX(),getY(),stack);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, getX(), getY());
    }

    public void renderTooltip(PoseStack matrices,int x,int y) {
        screen.renderTooltip(matrices,stack,x,y);
    }
}