package tfar.nabba.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SmallButton extends Button {

    public SmallButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback) {
        super(x, y, widthIn, heightIn, buttonText, callback,DEFAULT_NARRATION);
    }

    public boolean shouldDrawText() {
        return !getMessage().getString().isEmpty();
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,WIDGETS_LOCATION);

        int c = getYImage(isHovered);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);

        int halfwidth1 = this.width / 2;
        int halfwidth2 = this.width - halfwidth1;
        int halfheight1 = this.height / 2;
        int halfheight2 = this.height - halfheight1;
        blit(matrices, getX(), getY(), 0,
                46 + c * 20, halfwidth1, halfheight1);
        blit(matrices, getX() + halfwidth1, getY(), 200 - halfwidth2,
                46 + c * 20, halfwidth2, halfheight1);

        blit(matrices, getX(), getY() + halfheight1,
                0, 46 + c * 20 + 20 - halfheight2, halfwidth1, halfheight2);
        blit(matrices, getX() + halfwidth1, getY() + halfheight1,
                200 - halfwidth2, 46 + c * 20 + 20 - halfheight2, halfwidth2, halfheight2);
        if (shouldDrawText()) drawText(matrices, halfwidth2);
    }

    public void drawText(PoseStack stack, int halfwidth2) {
        int textColor = -1;
        drawCenteredString(stack, Minecraft.getInstance().font, getMessage(), getX() + halfwidth2, getY() + (this.height - 8) / 2, textColor);
    }
}