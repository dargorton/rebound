package net.backdoor.client.gui;

import net.backdoor.client.manager.ModuleManager;
import net.backdoor.client.mods.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DraggableWindow extends Screen {
    private int x, y, width, height;
    private boolean isDragging;
    private int dragX, dragY;
    public final List<ModuleButton> buttons = new ArrayList<>();

    public final List<SliderButton> sliders = new ArrayList<>();

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
        int borderColor = new Color(33, 33, 33).getRGB();
        int headerColor = new Color(168, 19, 19, 204).getRGB();
        int headerColor2 = new Color(168, 19, 19, 204).getRGB();
        // Draw window background and border
        context.fill(x, y, x + width, y + height, 0x80000000);  // semi-transparent dark background
        context.drawBorder(x, y, this.width + 1, this.height + 1, borderColor); // with border
        context.fillGradient(x, y, x + this.width, y + 20, headerColor, headerColor); // top bar gradient
        context.drawBorder(x, y, this.width + 1, 21, borderColor); // top bar border

        // Draw the title
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        context.drawTextWithShadow(textRenderer, this.getTitle(), x + 5, y + 5, 0xFFFFFF);

        // Render buttons
        for (ButtonWidget button : buttons) {
            button.render(context, mouseX, mouseY, delta);
        }

        for (SliderButton slider : sliders) {
            slider.render(context, mouseX, mouseY, delta);
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
            for (int i = 0; i < buttons.size(); i++) {
                updateAllButtonPos();
                /*if (buttons.get(i).settingsOn) {
                    updateAllButtonPos(0, this.buttons.indexOf(buttons.get(i)) + 1); // Update button positions based on settings
                } else {
                    updateAllButtonPos(0, 0);
                }*/
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void updateWindowHeight() {
        // Calculate the total height of the window based on the number of buttons
        int buttonHeight = 15; // button height but you probably want to actually check the font height
        int verticalSpacing = 0; // no extra vertical spacing between buttons
        int totalButtonHeight = (buttons.size() + sliders.size()) * buttonHeight; // total height without extra spacing with settings and stuff

        int headerHeight = 20; // height of the header

        // Set the window height based on the total button height + header
        this.height = totalButtonHeight + headerHeight;
    }

    public void updateAllButtonPos() {
        // Update the positions of buttons relative to the window's current position
        for (int index = 0; index < buttons.size(); index++) {
            int numSettings = buttons.get(index).thisModule.settings.size();

            if (buttons.get(index).settingsOn) {
                // Remove the condition that skips when index + 1 == buttons.size()
                for (int n = index + 1; n < buttons.size(); n++) {
                    System.out.println("doooooooooo it");
                    ButtonWidget button = buttons.get(n);
                    button.setX(x);  // Align X to the window's left edge
                    button.setY(y + 20 + ((numSettings + n) * 15));  // Adjust Y position dynamically with offset
                    button.setWidth(width); // Button width takes up the full window width
                }
            } else {
                ButtonWidget button = buttons.get(index);
                button.setX(x);  // Align X to the window's left edge
                button.setY(y + 20 + (index * 15));  // Adjust Y position dynamically with offset
                button.setWidth(width); // Button width takes up the full window width
            }
        }

        for (int i = 0; i < sliders.size(); i++) {
            SliderButton button = sliders.get(i);
            button.setX(x);  // Align X to the window's left edge
                                // relative position of button
            button.setY((button.getY() - y));  // Adjust Y position dynamically with offset
            button.setWidth(width); // Button width takes up the full window width
        }
    }


    public void addSlider(SliderButton slider) {
        this.addDrawableChild(slider);
        sliders.add(slider);
    }

    public void clearSliders() {
        for (SliderButton slider: sliders) {
            this.remove(slider);
        }
        sliders.clear();
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
