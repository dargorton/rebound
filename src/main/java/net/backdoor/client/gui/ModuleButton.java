
package net.backdoor.client.gui;

import net.backdoor.client.Backdoor;
import net.backdoor.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.backdoor.client.mods.Module;
import java.awt.*;
import java.util.ArrayList;

public class ModuleButton extends ButtonWidget {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private Module thisModule;
    private boolean settingsOn = false;
    protected ModuleButton(int x, int y, int width, int height, Text message, net.backdoor.client.mods.Module selModule, PressAction onPress) {
        super(x, y, width, height, message, onPress, null);
        thisModule = selModule;
    }

//    @FunctionalInterface
//    public interface RightClickAction {
//        void onRightClick(ButtonWidget button);
//    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int startColor;
        int endColor;
        if (isHovered()) {
            startColor = new Color(58, 6, 104).getRGB();
            endColor = new Color(58, 6, 104).getRGB();
        } else if (thisModule.enabled) {

            startColor = new Color(73, 8, 131).getRGB();
            endColor = new Color(73, 8, 131).getRGB();
        } else {
            startColor = new Color(46, 7, 81).getRGB();
            endColor = new Color(46, 7, 81).getRGB();
        }

        int borderColor = new Color(25, 6, 41).getRGB();

        context.drawTextWithShadow(client.textRenderer, getMessage(), getX() + 5, getY() + 2, Colors.WHITE);
        context.fillGradient(getX(), getY(), getX() + this.width, getY() + this.height, startColor, endColor);
        context.drawBorder(getX(), getY(), this.width + 1, this.height + 1, borderColor);
    }
    /*
        @Override
        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            this.setPosition((int) mouseX, (int) mouseY);
        }
    */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the button was right-clicked (right mouse button is button 1)
        if (button == 1 && this.isMouseOver(mouseX, mouseY)) {


            this.onRightClick();
            return true;


        } else if (button == 2 && this.isMouseOver(mouseX, mouseY)) {
            // logic here
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void onRightClick() {
        ArrayList<Setting<?>> settings = thisModule.settings;
        if (settings.isEmpty()) {
            System.out.println("NO SETTINGS!");
            return;
        }

        DraggableWindow categoryWindow = Backdoor.manager.getWindowFromCategory(thisModule.category);

        if (!settingsOn) {
            for (int i = 0; i < settings.size(); i++) {
                categoryWindow.updateAllButtonPos(15, categoryWindow.buttons.indexOf(this) + 1);
                //maybe this works
            }
        } else {
            categoryWindow.updateAllButtonPos(0,0);
        }
        settingsOn = !settingsOn;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }


}