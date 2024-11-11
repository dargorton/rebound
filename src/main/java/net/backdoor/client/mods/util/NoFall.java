package net.backdoor.client.mods.util;

import net.backdoor.client.mods.Category;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.backdoor.client.mods.Module;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class NoFall extends Module {

    private final MinecraftClient client = MinecraftClient.getInstance();
    public NoFall() {
        super("NoFall", Category.MISC, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        PlayerEntity player = client.player;

        if (enabled && player.fallDistance > 2.0F) {
           player.fallDistance = 0;
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
