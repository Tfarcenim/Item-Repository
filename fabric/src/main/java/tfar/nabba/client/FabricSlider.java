 /*
  * Copyright (c) Forge Development LLC and contributors
  * SPDX-License-Identifier: LGPL-2.1-only
  */

package tfar.nabba.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

/**
 * Slider widget implementation which allows inputting values in a certain range with optional step size.
 */
public class FabricSlider extends AbstractSliderButton
{
    protected Component prefix;
    protected Component suffix;

    protected double minValue;
    protected double maxValue;

    /** Allows input of discontinuous values with a certain step */
    protected double stepSize;

    protected boolean drawString;

    private final DecimalFormat format;

    /**
     * @param x x position of upper left corner
     * @param y y position of upper left corner
     * @param width Width of the widget
     * @param height Height of the widget
     * @param prefix {@link Component} displayed before the value string
     * @param suffix {@link Component} displayed after the value string
     * @param minValue Minimum (left) value of slider
     * @param maxValue Maximum (right) value of slider
     * @param currentValue Starting value when widget is first displayed
     * @param stepSize Size of step used. Precision will automatically be calculated based on this value if this value is not 0.
     * @param precision Only used when {@code stepSize} is 0. Limited to a maximum of 4 (inclusive).
     * @param drawString Should text be displayed on the widget
     */
    public FabricSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString)
    {
        super(x, y, width, height, Component.empty(), 0D);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = Math.abs(stepSize);
        this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.drawString = drawString;

        if (stepSize == 0D)
        {
            precision = Math.min(precision, 4);

            StringBuilder builder = new StringBuilder("0");

            if (precision > 0)
                builder.append('.');

            while (precision-- > 0)
                builder.append('0');

            this.format = new DecimalFormat(builder.toString());
        }
        else if (Mth.equal(this.stepSize, Math.floor(this.stepSize)))
        {
            this.format = new DecimalFormat("0");
        }
        else
        {
            this.format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }

        this.updateMessage();
    }

    /**
     * Overload with {@code stepSize} set to 1, useful for sliders with whole number values.
     */
    public FabricSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString)
    {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1D, 0, drawString);
    }

    /**
     * @return Current slider value as a double
     */
    public double getValue()
    {
        return this.value * (maxValue - minValue) + minValue;
    }

    /**
     * @return Current slider value as an long
     */
    public long getValueLong()
    {
        return Math.round(this.getValue());
    }

    /**
     * @return Current slider value as an int
     */
    public int getValueInt()
    {
        return (int) this.getValueLong();
    }

    /**
     * @param value The new slider value
     */
    public void setValue(double value)
    {
        this.value = this.snapToNearest((value - this.minValue) / (this.maxValue - this.minValue));
        this.updateMessage();
    }

    public String getValueString()
    {
        return this.format.format(this.getValue());
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.setValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
    {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseX);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        boolean flag = keyCode == GLFW.GLFW_KEY_LEFT;
        if (flag || keyCode == GLFW.GLFW_KEY_RIGHT)
        {
            if (this.minValue > this.maxValue)
                flag = !flag;
            float f = flag ? -1F : 1F;
            if (stepSize <= 0D)
                this.setSliderValue(this.value + (f / (this.width - 8)));
            else
                this.setValue(this.getValue() + f * this.stepSize);
        }

        return false;
    }

    private void setValueFromMouse(double mouseX)
    {
        this.setSliderValue((mouseX - (this.getX() + 4)) / (this.width - 8));
    }

    /**
     * @param value Percentage of slider range
     */
    private void setSliderValue(double value)
    {
        double oldValue = this.value;
        this.value = this.snapToNearest(value);
        if (!Mth.equal(oldValue, this.value))
            this.applyValue();

        this.updateMessage();
    }

    /**
     * Snaps the value, so that the displayed value is the nearest multiple of {@code stepSize}.
     * If {@code stepSize} is 0, no snapping occurs.
     */
    private double snapToNearest(double value)
    {
        if(stepSize <= 0D)
            return Mth.clamp(value, 0D, 1D);

        value = Mth.lerp(Mth.clamp(value, 0D, 1D), this.minValue, this.maxValue);

        value = (stepSize * Math.round(value / stepSize));

        if (this.minValue > this.maxValue)
        {
            value = Mth.clamp(value, this.maxValue, this.minValue);
        }
        else
        {
            value = Mth.clamp(value, this.minValue, this.maxValue);
        }

        return Mth.map(value, this.minValue, this.maxValue, 0D, 1D);
    }

    @Override
    protected void updateMessage()
    {
        if (this.drawString)
        {
            this.setMessage(Component.literal("").append(prefix).append(this.getValueString()).append(suffix));
        }
        else
        {
            this.setMessage(Component.empty());
        }
    }

    @Override
    protected void applyValue() {}

    private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");


    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        final Minecraft mc = Minecraft.getInstance();
        blitWithBorder(guiGraphics,SLIDER_LOCATION, this.getX(), this.getY(), 0, getTextureY(), this.width, this.height, 200, 20, 2, 3, 2, 2);

        blitWithBorder(guiGraphics,SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 0, getHandleTextureY(), 8, this.height, 200, 20 , 2, 3, 2, 2);

        renderScrollingString(guiGraphics, mc.font, 2, 0xffffff | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private int getTextureY() {
        int i = this.isFocused() && !this.canChangeValue ? 1 : 0;
        return i * 20;
    }

    private int getHandleTextureY() {
        int i = this.isHovered || this.canChangeValue ? 3 : 2;
        return i * 20;
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     *
     * @param texture the ResourceLocation object that contains the desired image
     * @param x x-axis offset
     * @param y y-axis offset
     * @param u bound resource location image x offset
     * @param v bound resource location image y offset
     * @param width the desired box width
     * @param height the desired box height
     * @param textureWidth the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param topBorder the size of the box's top border
     * @param bottomBorder the size of the box's bottom border
     * @param leftBorder the size of the box's left border
     * @param rightBorder the size of the box's right border
     */
    public void blitWithBorder(GuiGraphics graphics,ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder)
    {
        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        graphics.blit(texture, x, y, u, v, leftBorder, topBorder);
        // Top Right
        graphics.blit(texture, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder);
        // Bottom Left
        graphics.blit(texture, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder);
        // Bottom Right
        graphics.blit(texture, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            graphics.blit(texture, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder);
            // Bottom Border
            graphics.blit(texture, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                graphics.blit(texture, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight));
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            graphics.blit(texture, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight));
            // Right Border
            graphics.blit(texture, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight));
        }
    }
    
}
