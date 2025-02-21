package net.rebound.client.gui;

import net.rebound.client.devutil.FontAPI;
import net.rebound.client.manager.ModuleManager;
import net.rebound.client.mods.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DraggableWindow extends Screen {
    private int x, y, width, height;
    private boolean isDragging;
    private int dragX, dragY;
    public final List<ModuleButton> buttons = new ArrayList<>();

    public final List<SliderButton> sliders = new ArrayList<>();

    public final List<SettingButton> settingButtons = new ArrayList<>();

    public final Category category;

    private boolean isInit = false;

    // Constructor
    public DraggableWindow(int x, int y, int width, int height, String text, Category c) {
        super(Text.of(text));
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = c;
    }

    @Override
    public void init() {
        if (isInit) return;

        // List to hold buttons
        List<ModuleButton> moduleButtons = new ArrayList<>();

        // Loop through the modules in the given category and create buttons
        for (int i = 0; i < ModuleManager.getModulesFromCategory(this.category).size(); i++) {
            int finalI = i;
            ModuleButton button = new ModuleButton(
                    x, // x position (no padding)
                    y + 20 + (i * 15), // y position (calculated dynamically)
                    width, // full width of the window
                    15, // button height (fixed size)
                    Text.literal(ModuleManager.getModulesFromCategory(this.category).get(i).name),
                    ModuleManager.getModulesFromCategory(this.category).get(i),
                    click -> {
                        ModuleManager.getModulesFromCategory(this.category).get(finalI).toggle();
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
                    }
            );
            moduleButtons.add(button);
        }

        // Add all created buttons to the button list
        buttons.addAll(moduleButtons);

        // Adjust the window height based on the number of buttons
        updateWindowHeight();

        // Add buttons to the screen
        for (ButtonWidget button : buttons) {
            addDrawableChild(button);
        }



        isInit = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Header and background color
        int borderColor = new Color(65, 65, 65).getRGB();
        int headerColor = new Color(84, 81, 81, 255).getRGB();
        int headerColor2 = new Color(58, 56, 56, 255).getRGB();
        // Draw window background and border
        context.fill(x, y, x + width, y + height, 0x80000000);  // semi-transparent dark background
        context.drawBorder(x, y, this.width + 1, this.height + 1, borderColor); // with border
        context.fillGradient(x, y, x + this.width, y + 20, headerColor, headerColor); // top bar gradient
        context.drawBorder(x, y, this.width + 1, 21, borderColor); // top bar border

        // Draw the title
       //MinecraftClient client = MinecraftClient.getInstance();
       //TextRenderer textRenderer = client.textRenderer;
       //context.drawTextWithShadow(textRenderer, this.getTitle(), x + 5, y + 5, 0xFFFFFF);

        FontAPI.OSANS.drawString(context.getMatrices(), this.title.getLiteralString(), x +3 , y+2,
                1,1,1,1 );
        //FontAPI.renderer.drawText(context.getMatrices(),this.getTitle(),x + 5, y + 5,0xFFFFFF );
        // Render buttons
        for (ButtonWidget button : buttons) {
            button.render(context, mouseX, mouseY, delta);
        }

        for (SliderButton slider : sliders) {
            slider.render(context, mouseX, mouseY, delta);
        }

        for (SettingButton button : settingButtons) {
            button.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Start dragging the window if the header is clicked
        if (isInHeader(mouseX, mouseY) && button == 0) {
            isDragging = true;
            dragX = (int) mouseX - x;
            dragY = (int) mouseY - y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Stop dragging when the mouse is released
        if (button == 0) {
            isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            x = (int) mouseX - dragX;
            y = (int) mouseY - dragY;
            updateAllButtonPos();
            updateWindowHeight();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void updateWindowHeight() {
        // Calculate the total height of the window based on the number of buttons
        int buttonHeight = 15; // button height but you probably want to actually check the font height
        int verticalSpacing = 0; // no extra vertical spacing between buttons
        int totalButtonHeight = (buttons.size() + sliders.size() + settingButtons.size()) * buttonHeight; // total height without extra spacing with settings and stuff

        int headerHeight = 20; // height of the header

        // Set the window height based on the total button height + header
        this.height = totalButtonHeight + headerHeight;
    }

    public void updateAllButtonPos() {
        int currentY = y + 20;  // Starting Y position

        for (ModuleButton button : buttons) {
            int numSettings = button.thisModule.settings.size();

            button.setX(x);  // Align X to the window's left edge
            button.setY(currentY);  // Set current Y position for the button
            button.setWidth(width);  // Button width takes up the full window width

            // Move the Y position down by 15 units for each button
            currentY += 15;

            // If settings are enabled, add extra space for settings before moving to the next button
            if (button.settingsOn) {
                currentY += numSettings * 15;  // Add space for each setting
            } else {
                //currentY += numSettings * 15;
            }
        }

        for (SliderButton button : sliders) {
            button.setX(x);  // Align X to the window's left edge
            // relative position of button
            button.setY(y + button.relativePos);  // Adjust Y position dynamically with offset
            button.setWidth(width); // Button width takes up the full window width
        }

        for (int i = 0; i < settingButtons.size(); i++) {
            SettingButton button = settingButtons.get(i);
            button.setX(x);  // Align X to the window's left edge
            // relative position of button
            button.setY(y + button.relativePos);  // Adjust Y position dynamically with offset
            button.setWidth(width); // Button width takes up the full window width
        }
    }


    public void addSlider(SliderButton slider) {
        this.addDrawableChild(slider);
        sliders.add(slider);
    }

    public void addSettingButton(SettingButton button) {
        this.addDrawableChild(button);
        settingButtons.add(button);
    }

    public void clearSettings() {
        for (SliderButton slider: sliders) {
            this.remove(slider);
        }
        sliders.clear();
        for (SettingButton button: settingButtons) {
            this.remove(button);
        }
        settingButtons.clear();
    }

    // Method to check if the mouse is within the bounds of the window
    public boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private boolean isInHeader(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 20;
    }

    @Override
    public void close() {
        // Clear drawable children when the window is closed

        this.clearChildren();
        super.close();
    }
}
