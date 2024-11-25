package net.rebound.client.mods.movement;

import net.rebound.client.mods.Category;
import net.rebound.client.setting.Setting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.rebound.client.mods.Module;

import java.util.ArrayList;

public class Speed extends Module {
    public static Setting<Boolean> strafe = new Setting<>( // Setting for Strafe Mode
            "Strafe",
            "Sets the module to strafe mode",
            false,
            (args)->{}, // No validation needed
            null,
            null
    );

    public Speed() {
        super("Speed", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        this.settings.add(strafe);
    }

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        PlayerEntity player = client.player;

        if (strafe.getValue()) { // If Strafe is enabled, use Strafe mode
            new Strafe().onTick(client); // Use the Strafe class for movement
        } else if (enabled) { // Otherwise, use normal speed
            speed(client, player);
        }
    }

    private void speed(MinecraftClient client, PlayerEntity player) {
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d movementVec = Vec3d.ZERO;

        double speedMultiplier = 1.0;

        if (client.options.forwardKey.isPressed()) {
            speedMultiplier = 1.1;
            movementVec = movementVec.add(lookVec.multiply(speedMultiplier));
        }
        if (client.options.leftKey.isPressed()) {
            movementVec = movementVec.add(lookVec.rotateY(90).multiply(1.3)); // Move left
        }
        if (client.options.backKey.isPressed()) {
            movementVec = movementVec.add(lookVec.multiply(-1).multiply(1.3)); // Move backward
        }
        if (client.options.rightKey.isPressed()) {
            movementVec = movementVec.add(lookVec.rotateY(-90).multiply(1.3)); // Move right
        }
        if (!movementVec.equals(Vec3d.ZERO)) {
            player.setVelocity(movementVec.x, player.getVelocity().y, movementVec.z);
        }
    }

    // Strafe mode implementation
    static class Strafe {
        public void onTick(MinecraftClient client) {
            if (client == null || client.player == null) {
                return; // Exit early if conditions aren't met
            }

            PlayerEntity player = client.player;

            // Strafe mode allows forward movement with sprinting and jumping if on ground
            if (client.options.forwardKey.isPressed()) {
                player.setSprinting(true);
            }

            if (client.options.forwardKey.isPressed() && client.player.isOnGround()) {
                client.options.jumpKey.setPressed(true); // Simulate jump when moving forward
            }
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }

}
