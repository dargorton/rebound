package net.rebound.client;

import net.rebound.client.gui.DraggableWindow;
import net.rebound.client.gui.WindowManager;
import net.rebound.client.mods.Category;
import net.rebound.client.mods.Module;
import net.minecraft.client.gui.screen.Screen;
import net.rebound.client.manager.ModuleManager;
import net.rebound.client.mods.util.DiscordRPC;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;

public class Rebound implements ClientModInitializer {
    public static final String VERSION = "3.2+" + MinecraftClient.getInstance().getGameVersion();
    private KeyBinding keyBinding;
    public Screen currentScreen = MinecraftClient.getInstance().currentScreen;

    public static WindowManager manager;

    public DraggableWindow pvpWindow = new DraggableWindow(0, 100, 100, 150, "Pvp", Category.PVP);
    public DraggableWindow movementWindow = new DraggableWindow(100, 100, 100, 150, "Movement", Category.MOVEMENT);
    public DraggableWindow renderWindow = new DraggableWindow(200, 100, 100, 150, "Render", Category.RENDER);
    public DraggableWindow miscWindow = new DraggableWindow(300, 100, 100, 150, "Util", Category.MISC);

    public static MinecraftClient mc;
    @Override
    public void onInitializeClient() {
        this.mc = MinecraftClient.getInstance();

        try {
            DiscordRPC.startRPC();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        manager = new WindowManager();

        manager.addWindow(pvpWindow);
        manager.addWindow(movementWindow);
        manager.addWindow(renderWindow);
        manager.addWindow(miscWindow);

        ModuleManager.init();

        keyBinding = new KeyBinding(
                "ClickGUI",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N, //default
                "Rebound Client"
        );
        KeyBindingHelper.registerKeyBinding(keyBinding);

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        MinecraftClient client = MinecraftClient.getInstance();
        for (Module module : ModuleManager.list) {
            module.enabled = false;  // Ensure all modules are disabled on startup
        }

    }

    private void onClientTick(MinecraftClient client) {
        if (client != null && keyBinding.isPressed()) {
            client.execute(() -> {
                if (client.currentScreen == null) {
                    //client.setScreen(new ClickGUI(Text.literal("ClickGUI"), currentScreen));

                    /*pvpWindow.init();
                    movementWindow.init();
                    renderWindow.init();
                    utilWindow.init();*/

                    client.setScreen(manager);
                }
            });
        }
    }
}
