package tfar.nabba.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.nabba.client.FluidSpriteCache;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.net.C2SExtractFluidPacket;
import tfar.nabba.net.C2SInsertPacket;
import tfar.nabba.net.PacketHandler;

public class FluidStackWidget extends AbstractWidget {

    protected FluidStack stack = FluidStack.EMPTY;
    private final SearchableFluidScreen<?,?> screen;
    private int index;

    public FluidStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableFluidScreen<?,?> screen, int index) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.screen = screen;
        this.index = index;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(pButton)) {
                boolean flag = this.clicked(pMouseX, pMouseY);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(pMouseX, pMouseY);
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        final boolean shift = Screen.hasShiftDown();

        ItemStack carried = screen.getMenu().getCarried();

        if (carried.isEmpty()&& shift) {
            //shiftclicking on a slot should try to extract to fluid containers in inventory
            PacketHandler.sendToServer(new C2SExtractFluidPacket(index,shift));
        } else {
            carried.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandlerItem -> {
                boolean emptyContainer = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).isEmpty();
                if (emptyContainer && !stack.isEmpty()) {//try to take fluid
                    PacketHandler.sendToServer(new C2SExtractFluidPacket(index, shift));

                } else {//try to insert fluid
                    PacketHandler.sendToServer(new C2SInsertPacket(index));
                }

            });
        }


        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!stack.isEmpty()) {
            renderFluid(pPoseStack);
            if (isHovered) {
                renderTooltip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    public void renderFluid(PoseStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(stack.getFluid());
        int color = renderProperties.getTintColor(stack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(stack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f,1);
        RenderSystem.enableDepthTest();
        blit(matrices,x,y, 0, 16, 16, sprite);

        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x - .5, y + 11, 0);
        float scale = .75f;
        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-1 * x, -1 * y, 0);
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().font.draw(matrices,abrev(stack.getAmount()),x,y ,0xffffff);
        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();

    }

    private String abrev(int i) {
        return stack.getAmount()+"";
    }

    public void renderTooltip(PoseStack matrices,int x,int y) {
        screen.renderTooltip(matrices,stack.getDisplayName(),x,y);
    }

    public FluidStack getStack() {
        return stack;
    }

    public void setStack(FluidStack stack) {
        this.stack = stack;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
