package tfar.nabba.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.nabba.NABBA;
import tfar.nabba.menu.VanityKeyMenu;
import tfar.nabba.net.C2SSearchPacket;
import tfar.nabba.net.PacketHandler;

public class VanityKeyScreen extends AbstractContainerScreen<VanityKeyMenu> {
    public VanityKeyScreen(VanityKeyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public static final ResourceLocation TEXTURE = new ResourceLocation(NABBA.MODID,"textures/gui/container/vanity_key.png");
    private EditBox editBox;

    @Override
    protected void init() {
        super.init();
        initEditBox(82,6);
    }

    protected void initEditBox(int posX,int posY) {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.editBox = new EditBox(this.font, i +posX, j + posY, 103, 12, Component.translatable("container.nabba.anti_barrel"));
        this.editBox.setCanLoseFocus(false);
        this.editBox.setTextColor(-1);
        this.editBox.setTextColorUneditable(-1);
        this.editBox.setBordered(false);
        this.editBox.setMaxLength(10);
        this.editBox.setResponder(this::onNameChanged);
        this.editBox.setValue("");
        this.addWidget(this.editBox);
        this.setInitialFocus(this.editBox);
        this.editBox.setEditable(true);
    }

    private void onNameChanged(String string) {
        PacketHandler.sendToServer(new C2SSearchPacket(string));
    }


    @Override
    protected void renderBg(PoseStack stack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0,TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack,i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
