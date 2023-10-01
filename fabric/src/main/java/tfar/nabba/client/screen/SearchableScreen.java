package tfar.nabba.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.nabba.NABBA;
import tfar.nabba.inventory.ScrollbarWidgetC;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.net.PacketHandler;

public class SearchableScreen<S,T extends SearchableMenu<S>> extends AbstractContainerScreen<T> {

    private EditBox editBox;

    public SearchableScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight += 56;
        imageWidth+=18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        editBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ScrollbarWidgetC<>(leftPos + 174,topPos + 18,8,18 * 6 - 17,Component.literal("scroll"), this));
        initEditBox();
        PacketHandler.sendToServer(new C2SForceSyncPacket());
    }

    protected void initEditBox() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.editBox = new EditBox(this.font, i + 82, j + 6, 103, 12, Component.translatable("container.anti_barrel"));
        this.editBox.setTextColor(-1);
        this.editBox.setTextColorUneditable(-1);
        this.editBox.setBordered(false);
        this.editBox.setMaxLength(50);
        this.editBox.setResponder(this::onNameChanged);
        this.editBox.setValue("");
        this.addWidget(this.editBox);
        this.editBox.setEditable(true);
    }

    //@Override
    public void renderItemTooltip(GuiGraphics graphics, ItemStack pItemStack, int pMouseX, int pMouseY) {
        //super.renderTooltip(graphics, pItemStack, pMouseX, pMouseY);
        graphics.renderTooltip(this.font, this.getTooltipFromContainerItem(pItemStack), pItemStack.getTooltipImage(), pMouseX, pMouseY);
    }

    private void onNameChanged(String string) {
        PacketHandler.sendToServer(new C2SSearchPacket(string));
    }

    /**
     * Draws the background layer of this container (behind the items).
     *
     * @param partialTicks
     * @param mouseX
     * @param mouseY
     */
    @Override
    protected void renderBg(GuiGraphics stack,float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        stack.blit(TEXTURE,i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    public boolean canScroll() {
        return menu.getFilledSlotCount() > 54;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (canScroll()) {
            PacketHandler.sendToServer(PacketHandler.scroll,buf -> buf.writeInt((int) pDelta));
        }

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }

        return this.editBox.keyPressed(pKeyCode, pScanCode, pModifiers) || this.editBox.canConsumeInput() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    public static final ResourceLocation TEXTURE = new ResourceLocation(NABBA.MODID,"textures/gui/container/anti_barrel.png");

}
