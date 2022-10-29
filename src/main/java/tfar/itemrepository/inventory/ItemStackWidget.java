package tfar.itemrepository.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.itemrepository.RepositoryScreen;
import tfar.itemrepository.net.C2SInsertPacket;
import tfar.itemrepository.net.C2SRequestPacket;
import tfar.itemrepository.net.PacketHandler;

public class ItemStackWidget extends AbstractWidget {

    protected ItemStack stack = ItemStack.EMPTY;
    private final RepositoryScreen screen;
    private int index;

    public ItemStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, RepositoryScreen screen, int index) {
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
            PacketHandler.sendToServer(new C2SInsertPacket());
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            if (isHovered) {
                fill(pPoseStack, x, y, x + 16, y + 16, 0x80ffffff);
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
            renderItem(pPoseStack);
        }
    }

    public void renderItem(PoseStack matrices) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack,x,y);
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
