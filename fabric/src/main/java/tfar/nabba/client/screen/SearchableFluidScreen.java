package tfar.nabba.client.screen;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.api.SearchableFluidHandler;
import tfar.nabba.client.gui.screens.SearchableScreen;
import tfar.nabba.client.screen.button.FluidStackWidget;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.util.FabricFluidStack;

import java.util.List;
import java.util.Optional;

public class SearchableFluidScreen<T extends SearchableFluidHandler,U extends SearchableFluidMenu<T>> extends SearchableScreen<FabricFluidStack,U> {

    private final FluidStackWidget[] widgets = new FluidStackWidget[54];
    public SearchableFluidScreen(U pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int xPos = leftPos + 8;
        int yPos = topPos + 18;
        for (int y = 0; y < 6;y++) {
            for (int x = 0; x < 9;x++) {
                int index = x + 9 * y;
                FluidStackWidget widget = new FluidStackWidget(xPos+ 18 * x,yPos + 18 * y, 18, 18, Component.literal("test"),
                        this);
                widgets[index] = widget;
                addRenderableWidget(widget);
            }
        }
    }

    public void renderFluidTooltip(GuiGraphics graphics, FabricFluidStack stack, int pMouseX, int pMouseY) {
        graphics.renderTooltip(this.font, FluidVariantRendering.getTooltip(stack.getFluidVariant()),
                Optional.empty(), pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
        pPoseStack.drawString(font,menu.getFilledSlotCount()+"", this.titleLabelX + 60, this.inventoryLabelY, 0x404040,false);
    }

    public void setGuiFluids(List<FabricFluidStack> stacks) {
        for (int i = 0; i < 54;i++) {
            if (i < stacks.size()) {
                widgets[i].setStack(stacks.get(i));
            } else {
                widgets[i].setStack(FabricFluidStack.empty());
            }
        }
    }
}
