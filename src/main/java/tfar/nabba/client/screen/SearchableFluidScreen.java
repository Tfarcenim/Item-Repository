package tfar.nabba.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import tfar.nabba.api.SearchableFluidHandler;
import tfar.nabba.inventory.FluidStackWidget;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.util.Utils;

import java.util.List;

public class SearchableFluidScreen<T extends SearchableFluidHandler,U extends SearchableFluidMenu<T>> extends SearchableScreen<U> {

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
                        this,menu.getDisplaySlot(index));
                widgets[index] = widget;
                addRenderableWidget(widget);
            }
        }
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
        this.font.draw(pPoseStack, menu.getFilledSlotCount()+"", (float)this.titleLabelX + 60, (float)this.inventoryLabelY, 0x404040);
    }

    public void setGuiFluids(List<FluidStack> stacks, List<Integer> ints) {
        for (int i = 0; i < 54;i++) {
            if (i < stacks.size()) {
                widgets[i].setStack(stacks.get(i));
                widgets[i].setIndex(ints.get(i));
            } else {
                widgets[i].setStack(FluidStack.EMPTY);
                widgets[i].setIndex(Utils.INVALID);
            }
        }
    }
}
