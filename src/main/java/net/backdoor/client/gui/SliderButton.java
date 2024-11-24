package net.backdoor.client.gui;

import net.backdoor.client.Backdoor;
import net.backdoor.client.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.backdoor.client.mods.Module;

import java.awt.*;

public class SliderButton extends SliderWidget {

    public final Setting setting;

    private final int min;
    private final int max;

    public final int relativePos;

    private int currentValue;

    public SliderButton(Setting setting, int x, int y, int width, int height, int relativePos, double lengthValue, int min, int max) {
        super(x, y, width, height, Text.literal("test"), 0.0);
        this.setting = setting;
        this.relativePos = relativePos;
        this.min = min;
        this.max = max;
        this.value = lengthValue;
        if (setting != null) {
            this.currentValue = (int) setting.getValue();
        }
        this.updateMessage();
    }



    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int startColor;
        int endColor;
        if (this.isHovered()) {
            startColor = 0x80000000; // background
            endColor = 0x80000000;
        } else {
            startColor = new Color(28, 28, 30, 0).getRGB(); // background
            endColor = new Color(28, 28, 30, 115).getRGB();
        }

        int startSliderColor = new Color(236, 38, 38, 80).getRGB(); // slider
        int endSliderColor  = new Color(236, 38, 38, 80).getRGB();

        int borderColor = new Color(33, 33, 33).getRGB();

        context.drawTextWithShadow(Backdoor.mc.textRenderer, getMessage(), getX() + 5, getY() + 2, Colors.WHITE);
        context.fillGradient(getX(), getY(), getX() + this.width, getY() + this.height, startColor, endColor);
        context.fillGradient(getX(), getY(), getX() + (int)(this.width*value), getY() + this.height, startSliderColor, endSliderColor);
        context.drawBorder(getX(), getY(), this.width + 1, this.height + 1, borderColor);
    }


    @Override
    protected void updateMessage() {
        // Update displayed value based on slider position
        int value = (int) (min + this.value * (max - min));
        setMessage(Text.literal(setting.name + ": " + value));
    }

    @Override
    protected void applyValue() {
        // Update the currentValue in the parent screen
        currentValue = (int) (min + this.value * (max - min));
        setting.lengthValue = value;
        if (setting != null) {
            setting.setValue(currentValue);
        }
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }
}
