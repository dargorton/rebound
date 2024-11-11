package net.backdoor.client.mods.util;

import net.backdoor.client.mods.Category;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.backdoor.client.mods.Module;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class Velocity extends Module {

    public Velocity() {
        super("Velocity", Category.MISC, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }
        PlayerEntity player = client.player;

        if (enabled) {
            Vec3d velocity = player.getVelocity();
            double newYVelocity = velocity.y - 0.5;
            if (newYVelocity < -5) {
                newYVelocity = -5; }

            player.setVelocity(velocity.x, newYVelocity, velocity.z);
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }

}
