package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class ClientSlottedTooltip<T> implements ClientTooltipComponent {

    protected final T stack;

    public ClientSlottedTooltip(T stack) {
        this.stack = stack;
    }

    @Override
    public void renderImage(Font font, int i, int j, GuiGraphics graphics) {
        this.renderSlot(i, j, 0, font, graphics);
    }

    protected void renderSlot(int i, int j, int slot, Font font, GuiGraphics graphics) {
        this.blit(graphics, i, j, 0);
    }

    protected void blit(GuiGraphics poseStack, int i, int j, int k) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.blit(ClientBundleTooltip.TEXTURE_LOCATION,i, j, 0, 0, 0, 18, 18, 128, 128);
    }

    @Override
    public int getHeight() {
        return 18 + 4;
    }

    @Override
    public int getWidth(Font pFont) {
        return 18;
    }
}
