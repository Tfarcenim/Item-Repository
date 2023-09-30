package tfar.nabba.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class BackgroundEditBox extends EditBox {

    private int bgColor = 0xffffffff;

    public BackgroundEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage);
    }

   // @Override
   // protected void renderBg(GuiGraphics pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
   //     pPoseStack.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, getBgColor());
   // }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
