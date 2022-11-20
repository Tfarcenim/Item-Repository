package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class ClientSlottedTooltip<T> implements ClientTooltipComponent {

    protected final T stack;

    public ClientSlottedTooltip(T stack) {
        this.stack = stack;
    }

    @Override
    public void renderImage(Font font, int i, int j, PoseStack poseStack, ItemRenderer itemRenderer, int k) {
        this.renderSlot(i, j, 0, font, poseStack, itemRenderer, k);
    }

    protected void renderSlot(int i, int j, int slot, Font font, PoseStack poseStack, ItemRenderer itemRenderer, int l) {
        this.blit(poseStack, i, j, l);
    }

    protected void blit(PoseStack poseStack, int i, int j, int k) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ClientBundleTooltip.TEXTURE_LOCATION);
        GuiComponent.blit(poseStack, i, j, 0, 0, 0, 18, 18, 128, 128);
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
