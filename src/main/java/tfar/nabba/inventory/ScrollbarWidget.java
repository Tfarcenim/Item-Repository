package tfar.nabba.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import tfar.nabba.client.screen.AntiBarrelScreen;

public class ScrollbarWidget extends AbstractWidget {
    private final AntiBarrelScreen screen;

    public ScrollbarWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, AntiBarrelScreen screen) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.screen = screen;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, AntiBarrelScreen.TEXTURE);
        int rows = screen.getMenu().getSearchRows();

        if (rows > 6) {
            int currentRow = screen.getMenu().getCurrentRow();
            double frac = (double)currentRow / (rows - 6);
            double position = height * frac;
            blit(pPoseStack, x, (int) (y + position), 244, 0, 12, 15);
        }
    }



    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
