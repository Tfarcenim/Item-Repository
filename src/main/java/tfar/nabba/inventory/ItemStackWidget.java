package tfar.nabba.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.net.C2SInsertPacket;
import tfar.nabba.net.C2SExtractItemPacket;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.Utils;

public class ItemStackWidget extends RightClickButton<ItemStack> {

    private final SearchableItemScreen<?,?> screen;

    public ItemStackWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableItemScreen<?,?> screen, int index) {
        super(pX, pY, pWidth, pHeight, pMessage, screen, index);
        this.screen = screen;
        stack = ItemStack.EMPTY;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        boolean shift = Screen.hasShiftDown();

        if (screen.getMenu().getCarried().isEmpty() &&!stack.isEmpty()) {//try to take item
            PacketHandler.sendToServer(new C2SExtractItemPacket(index, stack.getMaxStackSize(), shift));
        } else if (!screen.getMenu().getCarried().isEmpty()){//try to insert item
            PacketHandler.sendToServer(new C2SInsertPacket(index));
        }
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRightClick(double pMouseX, double pMouseY) {
        if (!screen.getMenu().getCarried().isEmpty() && stack.isEmpty()) {//try to add item
            PacketHandler.sendToServer(new C2SInsertPacket(index));
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

        PoseStack viewModelPose = RenderSystem.getModelViewStack();
        viewModelPose.pushPose();
        viewModelPose.translate(x + 8, y + 8, 0);
        float scale = .5f;

        viewModelPose.scale(scale, scale, scale);
        viewModelPose.translate(-1 * x, -1 * y, 1000);
        RenderSystem.applyModelViewMatrix();
        if (stack.getCount() > 1) {
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack,x,y, Utils.formatLargeNumber(stack.getCount()));
        }

        viewModelPose.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void renderTooltip(PoseStack matrices,int x,int y) {
        screen.renderTooltip(matrices,stack,x,y);
    }
}