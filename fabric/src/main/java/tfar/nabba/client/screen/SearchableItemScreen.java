package tfar.nabba.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.client.gui.screens.SearchableScreen;
import tfar.nabba.client.screen.button.ItemStackWidget;
import tfar.nabba.menu.SearchableItemMenu;

import java.util.List;

public class SearchableItemScreen<T extends SearchableItemHandler,U extends SearchableItemMenu<T>> extends SearchableScreen<ItemStack,U> {

    private final ItemStackWidget[] widgets = new ItemStackWidget[54];
    public SearchableItemScreen(U pMenu, Inventory pPlayerInventory, Component pTitle) {
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
                ItemStackWidget widget = new ItemStackWidget(xPos+ 18 * x,yPos + 18 * y, 18, 18, Component.literal("test"),
                        this);
                widgets[index] = widget;
                addRenderableWidget(widget);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
        pPoseStack.drawString(font,menu.getFilledSlotCount()+"", this.titleLabelX + 60, this.inventoryLabelY, 0x404040,false);
    }

    public void setGuiStacks(List<ItemStack> stacks) {
        for (int i = 0; i < 54;i++) {
            if (i < stacks.size()) {
                widgets[i].setStack(stacks.get(i));
            } else {
                widgets[i].setStack(ItemStack.EMPTY);
            }
        }
    }
}