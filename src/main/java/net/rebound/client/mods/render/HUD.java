package net.rebound.client.mods.render;

import net.rebound.client.mods.Category;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.rebound.client.mods.Module;

import java.util.ArrayList;

public class HUD extends Module {

    public HUD() {
        super("HUD", Category.RENDER, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) { /*
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            int textcolor = new Color(23, 84, 207).getRGB();
            context.drawText(client.textRenderer, "Backdoor v2.7+" + client.getGameVersion(), 1, 2, textcolor, true);
        });
    */ }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }

}
