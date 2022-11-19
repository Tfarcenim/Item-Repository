package tfar.nabba.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import tfar.nabba.client.screen.SearchableScreen;

public class RightClickButton<T> extends AbstractWidget {

    protected int index;
    protected T stack;
    protected final SearchableScreen<?> screen;

    public RightClickButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, SearchableScreen<?> screen,int index) {
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
                    if (pButton == 0) {
                        this.onClick(pMouseX, pMouseY);
                    } else if (pButton == 1) {
                        onRightClick(pMouseX, pMouseY);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return pButton == 0 || pButton == 1;
    }

    public void onRightClick(double pMouseX, double pMouseY) {
        //onClick(pMouseX, pMouseY);
    }

    public T getStack() {
        return stack;
    }

    public void setStack(T stack) {
        this.stack = stack;
    }


    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
