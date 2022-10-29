package tfar.itemrepository;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tfar.itemrepository.inventory.ItemStackWidget;
import tfar.itemrepository.net.C2SGetDisplayPacket;
import tfar.itemrepository.net.C2SScrollPacket;
import tfar.itemrepository.net.PacketHandler;

import java.util.List;

public class RepositoryScreen extends AbstractContainerScreen<RepositoryMenu> {

    private final ItemStackWidget[] widgets = new ItemStackWidget[54];
    public RepositoryScreen(RepositoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight += 56;
        this.inventoryLabelY = this.imageHeight - 94;
        PacketHandler.sendToServer(new C2SGetDisplayPacket());
    }
    private static final ResourceLocation TEXTURE = new ResourceLocation(ItemRepository.MODID,"textures/gui/container/repository.png");

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public void renderTooltip(PoseStack pPoseStack, ItemStack pItemStack, int pMouseX, int pMouseY) {
        super.renderTooltip(pPoseStack, pItemStack, pMouseX, pMouseY);
    }

    @Override
    protected void init() {
        super.init();
        int xPos = leftPos + 8;
        int yPos = topPos + 18;
        for (int y = 0; y < 6;y++) {
            for (int x = 0; x < 9;x++) {
                int index = x + 9 * y;
                ItemStackWidget widget = new ItemStackWidget(xPos+ 18 * x,yPos + 18 * y, 18, 18, Component.literal("test"),
                        this,menu.getDisplaySlot(index));
                widgets[index] = widget;
                addRenderableWidget(widget);
            }
        }
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        List<Component> tooltipFromItem = super.getTooltipFromItem(itemStack);
        if (itemStack.getMaxStackSize() != 1) {
            tooltipFromItem.add(Component.literal("Can't be stored").withStyle(ChatFormatting.RED));
        }
        return tooltipFromItem;
    }

    /**
     * Draws the background layer of this container (behind the items).
     *
     * @param partialTicks
     * @param mouseX
     * @param mouseY
     */
    @Override
    protected void renderBg(PoseStack stack,float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0,TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack,i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
        this.font.draw(pPoseStack, "" + menu.getSlotCount(), (float)this.titleLabelX + 60, (float)this.titleLabelY, 0x404040);
    }

    public void setGuiStacks(List<ItemStack> stacks, List<Integer> ints) {
        for (int i = 0; i < 54;i++) {
            if (i < stacks.size()) {
                widgets[i].setStack(stacks.get(i));
                widgets[i].setIndex(ints.get(i));
            } else {
                widgets[i].setStack(ItemStack.EMPTY);
            }
        }
    }

    public boolean canScroll() {
        return menu.getSlotCount() > 54;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {

        if (canScroll()) {
            PacketHandler.sendToServer(new C2SScrollPacket((int) pDelta));
        }

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
}
