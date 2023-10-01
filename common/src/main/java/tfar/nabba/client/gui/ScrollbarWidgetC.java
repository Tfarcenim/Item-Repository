package tfar.nabba.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import tfar.nabba.client.gui.screens.SearchableScreen;
import tfar.nabba.menu.SearchableMenu;

public class ScrollbarWidgetC<S,T extends SearchableMenu<S>> extends AbstractWidget {
    private final SearchableScreen<S,T> screen;

    public ScrollbarWidgetC(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableScreen<S,T> screen) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.screen = screen;
    }

    @Override
    public void renderWidget(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        int rows = screen.getMenu().getSearchRows();

        if (rows > 6) {
            int currentRow = screen.getMenu().getCurrentRow();
            double frac = (double)currentRow / (rows - 6);
            double position = height * frac;
            pPoseStack.blit(SearchableScreen.TEXTURE,getX(), (int) (getY() + position), 244, 0, 12, 15);
        }
    }



    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
