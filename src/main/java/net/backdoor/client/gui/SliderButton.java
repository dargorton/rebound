package net.backdoor.client.gui;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class SliderButton extends SliderWidget {
    private final int min;
    private final int max;

    private int currentValue;

    public SliderButton(int x, int y, int width, int height, int min, int max) {
        super(x, y, width, height, Text.literal("test"), 0.0);
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
