package tfar.nabba.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.AntiBarrelScreen;
import tfar.nabba.net.C2SInsertPacket;
import tfar.nabba.net.C2SRequestPacket;
import tfar.nabba.net.PacketHandler;

public class ItemStackWidget extends AbstractWidget {

    protected ItemStack stack = ItemStack.EMPTY;
    private final AntiBarrelScreen screen;
    private int index;

    public ItemStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, AntiBarrelScreen screen, int index) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.screen = screen;
        this.index = index;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        boolean shift = Screen.hasShiftDown();

        if (screen.getMenu().getCarried().isEmpty() &&!stack.isEmpty()) {//try to take item
            PacketHandler.sendToServer(new C2SRequestPacket(index, 1, shift));
        } else {//try to insert item
            PacketHandler.sendToServer(new C2SInsertPacket(0));
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            if (isHovered) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                fill(pPoseStack, x, y, x + 16, y + 16, 0x80ffffff);
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
            renderItem(pPoseStack);
        }
    }

    public void renderItem(PoseStack matrices) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack,x,y);
        Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack,x,y);
    }

    public void renderTooltip(PoseStack matrices,int x,int y) {
        screen.renderTooltip(matrices,stack,x,y);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
