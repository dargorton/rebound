package net.backdoor.client.mods.movement;

import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class AutoSprint extends Module {


    public AutoSprint() {
        super("AutoSprint", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (enabled && client.player != null) {
            client.player.setSprinting(true);
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}