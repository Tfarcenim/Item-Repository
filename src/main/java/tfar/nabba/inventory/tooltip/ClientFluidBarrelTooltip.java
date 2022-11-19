package tfar.nabba.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.util.ClientUtils;

public class ClientFluidBarrelTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int TEX_SIZE = 128;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 18;
    private final FluidStack stack;
    public ClientFluidBarrelTooltip(FluidBarrelTooltip bundleTooltip) {
        this.stack = bundleTooltip.getStack();
    }

    @Override
    public int getHeight() {
        return 18 + 4;
    }

    @Override
    public int getWidth(Font font) {
        return 18;
    }

    @Override
    public void renderImage(Font font, int i, int j, PoseStack poseStack, ItemRenderer itemRenderer, int k) {
        this.renderSlot(i, j, 0, font, poseStack, itemRenderer, k);
    }

    private void renderSlot(int i, int j, int slot, Font font, PoseStack poseStack, ItemRenderer itemRenderer, int l) {
        RenderSystem.disableDepthTest();
          this.blit(poseStack, i, j, l, Texture.SLOT);
        ClientUtils.renderFluid1(poseStack,i+1,j+1,stack);
    }

    private void blit(PoseStack poseStack, int i, int j, int k, Texture texture) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(poseStack, i, j, 0, texture.x, texture.y, texture.w, texture.h, 128, 128);
    }

    enum Texture {
        SLOT(0, 0, 18, 18);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        Texture(int j, int k, int l, int m) {
            this.x = j;
            this.y = k;
            this.w = l;
            this.h = m;
        }
    }
}
