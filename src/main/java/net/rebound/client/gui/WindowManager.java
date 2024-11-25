package net.rebound.client.gui;

import net.rebound.client.mods.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class WindowManager extends Screen {
    private final List<DraggableWindow> windows = new ArrayList<>();

    public WindowManager() {
        super(Text.of("Window Manager"));
    }

    public void addWindow(DraggableWindow window) {
        if (!windows.contains(window)) {
            windows.add(window);
        }
    }

    public DraggableWindow getWindowFromCategory(Category category) {
        for (DraggableWindow window : windows) {
            if (window.category == category) {
                return window;
            }
        }
        return null;
    }

    @Override
    public void init() {

        client.options.getMenuBackgroundBlurriness().setValue(0);

        for (DraggableWindow window : windows) {
            //window.render(context, mouseX, mouseY, delta);
            window.init();
        }
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render each window in order
        super.render(context, mouseX, mouseY, delta);

        for (DraggableWindow window : windows) {
            window.render(context, mouseX, mouseY, delta);
            //window.init();
        }
        //super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Iterate from top to bottom to handle clicks
        for (int i = windows.size() - 1; i >= 0; i--) {
            DraggableWindow window = windows.get(i);
            if (window.isWithinBounds(mouseX, mouseY)) {
                // Bring the clicked window to the front
                windows.remove(window);
                windows.add(window);
                return window.mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Pass mouse release events to each window
        for (DraggableWindow window : windows) {
            if (window.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Pass mouse drag events to each window
        for (DraggableWindow window : windows) {
            if (window.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void close() {
        // Clear drawable children to release resources
        this.clearChildren();
        super.close();
    }

}