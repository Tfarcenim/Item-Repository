package tfar.nabba.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.lwjgl.glfw.GLFW;
import tfar.nabba.NABBA;
import tfar.nabba.blockentity.AbstractBarrelBlockEntity;
import tfar.nabba.inventory.BackgroundEditBox;
import tfar.nabba.menu.VanityKeyMenu;
import tfar.nabba.net.C2SVanityPacket;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.util.Utils;

public class VanityKeyScreen extends AbstractContainerScreen<VanityKeyMenu> {

    final Player player;
    public VanityKeyScreen(VanityKeyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.inventoryLabelY = this.imageHeight - 94;
        player = pPlayerInventory.player;
    }

    private int localColor;

    public static final ResourceLocation TEXTURE = new ResourceLocation(NABBA.MODID,"textures/gui/container/vanity_key.png");
    private BackgroundEditBox editBox;

    private ForgeSlider slider;

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        editBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        BlockEntity blockEntity =  player.level.getBlockEntity(menu.getPos());
        int initialColor = Utils.COLOR;
        double initialSize = Utils.SIZE;
        if (blockEntity instanceof AbstractBarrelBlockEntity abstractBarrelBlockEntity) {
            initialColor = abstractBarrelBlockEntity.getColor();
            initialSize = abstractBarrelBlockEntity.getSize();
        }

        slider = new ForgeSlider(i + 38,j + 46,100,20,Component.empty(),Component.empty(),
                0,1,initialSize,.01,0,true){
            @Override
            public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
                syncVanity();
                return super.mouseReleased(pMouseX, pMouseY, pButton);
            }
        };
        addRenderableWidget(slider);
        initEditBox(58,28,initialColor);
    }

    protected void initEditBox(int posX,int posY,int color) {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.editBox = new BackgroundEditBox(this.font, i +posX, j + posY, 103, 12, Component.translatable("container.nabba.vanity_key"));
        this.editBox.setCanLoseFocus(false);
        this.editBox.setTextColor(-1);
        this.editBox.setTextColorUneditable(-1);
        this.editBox.setBordered(false);
        this.editBox.setMaxLength(6);
        this.editBox.setResponder(this::onNameChanged);
        this.editBox.setValue(Integer.toHexString(color));
        this.addWidget(this.editBox);
        this.setInitialFocus(this.editBox);
        this.editBox.setEditable(true);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (slider.isMouseOver(pMouseX,pMouseY)) {
            return slider.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private void onNameChanged(String string) {
        editBox.setBgColor(validColor(string) ? 0xff00ff00 : 0xffff0000);
        try {
            localColor = Integer.parseInt(string,16);
            syncVanity();
        } catch (NumberFormatException e) {
            //whatever
        }
    }

    protected boolean validColor(String color) {
        try {
            Integer.parseInt(color,16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0,TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack,i, j, 0, 0, this.imageWidth, this.imageHeight);

        int xPos = 134;
        int yPos = 27;

        fill(stack,leftPos+ xPos,topPos + yPos,i +xPos + 10 , j +yPos+ 10, 0xff000000 | localColor);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
        this.font.draw(pPoseStack, Component.literal("Color:"), this.titleLabelX, this.titleLabelY + 24, 0x404040);
        this.font.draw(pPoseStack, Component.literal("Size:"), this.titleLabelX, this.titleLabelY + 45, 0x404040);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }

        return this.editBox.keyPressed(pKeyCode, pScanCode, pModifiers) || this.editBox.canConsumeInput() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    protected void syncVanity() {
        PacketHandler.sendToServer(new C2SVanityPacket(localColor,slider.getValue()));
    }

    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
}
