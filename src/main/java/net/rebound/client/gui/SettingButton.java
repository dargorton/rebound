package net.rebound.client.gui;

import net.rebound.client.devutil.FontAPI;
import net.rebound.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.awt.*;

public class SettingButton extends ButtonWidget {

    private final MinecraftClient client = MinecraftClient.getInstance();
    public Setting setting;

    public int relativePos;

    public boolean settingsOn = false;
    protected SettingButton(Setting setting, int x, int y, int width, int height, int relativePos, PressAction onPress) {
        super(x, y, width, height, Text.of("m"), onPress, null);
        this.relativePos = relativePos;
        this.setting = setting;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int startColor;
        int endColor;
        if (isHovered()) {
            startColor = new Color(28, 28, 30).getRGB();
            endColor = new Color(28, 28, 30).getRGB();
        } else if (setting.isValueTrue()) { // the setting is on
            startColor = new Color(47, 47, 49).getRGB();
            endColor = new Color(47, 47, 49).getRGB();
        } else {
            startColor = 0x8000000;
            endColor = 0x80000000;
        }

        int borderColor = new Color(33, 33, 33).getRGB();

        //context.drawTextWithShadow(client.textRenderer, this.setting.name,
        // getX() + 5, getY() + 2, Colors.WHITE);
        FontAPI.OSANS.drawString(context.getMatrices(), this.setting.name,
                getX() +5, getY() + 2, 1,1,1,1);
        context.fillGradient(getX(), getY(), getX() + this.width, getY() + this.height, startColor, endColor);
        context.drawBorder(getX(), getY(), this.width + 1, this.height + 1, borderColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the button was right-clicked (right mouse button is button 1)
        if (this.isMouseOver(mouseX, mouseY)) {
            if (button == 1) {  // Right-click
                // toggle logic here
                return true;
            } else if (button == 2) {  // Middle-click

                return true;
            } else if (button == 0) {  // Left-click
                this.setting.setValue(!(boolean)this.setting.getValue());
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }
}
