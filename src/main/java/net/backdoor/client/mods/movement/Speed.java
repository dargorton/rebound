package net.backdoor.client.mods.movement;

import net.backdoor.client.mods.Category;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.backdoor.client.mods.Module;

import java.util.ArrayList;

public class Speed extends Module {
    public Speed() {
        super("Speed", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        PlayerEntity player = client.player;
        if (enabled) {
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d movementVec = Vec3d.ZERO;

            double speedMultiplier = 1.0;

            if (client.options.forwardKey.isPressed()) {
                speedMultiplier = 1.5;
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
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
