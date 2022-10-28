package tfar.itemrepository;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RepositoryScreen extends AbstractContainerScreen<RepositoryMenu> {
    public RepositoryScreen(RepositoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight += 56;
        this.inventoryLabelY = this.imageHeight - 94;;
    }
    private static final ResourceLocation TEXTURE = new ResourceLocation(ItemRepository.MODID,"textures/gui/container/repository.png");

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
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
        this.font.draw(pPoseStack, "" + menu.getRealSlots(), (float)this.titleLabelX + 60, (float)this.titleLabelY, 0x404040);
    }
}
