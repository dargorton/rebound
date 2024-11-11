package net.backdoor.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CustomSlider extends Screen {
    private final int minValue;
    private final int maxValue;
    private int currentValue;
    private final int x, y, width, height;
    private ValueSlider slider;

    // Constructor to initialize min, max values, and slider position and size
    public CustomSlider(int minValue, int maxValue, int x, int y, int width, int height) {
        super(Text.literal("Custom Slider Screen"));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentValue = minValue; // Start at minimum value
    }

    @Override
    protected void init() {
        // Initialize slider with custom position and size
        slider = new ValueSlider(x, y, width, height, minValue, maxValue);
        this.addDrawableChild(slider); // Adds the slider to the screen
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the screen and display the current value near the slider
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(this.textRenderer, "Value: " + currentValue, x + width / 2, y - 12, 0xFFFFFF);
    }

    // Inner class to handle the slider widget
    private class ValueSlider extends SliderWidget {
        private final int min;
        private final int max;

        public ValueSlider(int x, int y, int width, int height, int min, int max) {
            super(x, y, width, height, Text.literal(""), 0.0);
            this.min = min;
            this.max = max;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            // Update displayed value based on slider position
            int value = (int) (min + this.value * (max - min));
            setMessage(Text.literal(String.valueOf(value)));
        }

        @Override
        protected void applyValue() {
            // Update the currentValue in the parent screen
            currentValue = (int) (min + this.value * (max - min));
        }
    }
}