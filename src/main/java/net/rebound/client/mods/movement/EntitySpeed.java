package net.rebound.client.mods.movement;

import net.rebound.client.mods.Category;
import net.rebound.client.mods.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class EntitySpeed extends Module {
    public EntitySpeed() {
        super("BoatFly", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        PlayerEntity p = client.player;
        if (p == null || !p.hasVehicle()) {
            return;
        }

        if (!(p.getVehicle() instanceof VehicleEntity vehicle)) {
            return;
        }

        Vec3d lookVec = p.getRotationVec(1.0F);
        Vec3d movementVec = Vec3d.ZERO;

        double speedMultiplier = 1.0;
        double upMult = 0.0;

        if (enabled) {
            if (client.options.forwardKey.isPressed()) {
                speedMultiplier = 2.0;
                movementVec = movementVec.add(lookVec.multiply(speedMultiplier));
            }

            if (client.options.backKey.isPressed()) {
                movementVec = movementVec.add(lookVec.multiply(-1).multiply(1.5));
            }

            if (!movementVec.equals(Vec3d.ZERO)) {
                vehicle.setVelocity(movementVec.x, vehicle.getVelocity().y, movementVec.z);
            }

            if (client.options.jumpKey.isPressed()) {
                upMult = 1.1;
            }

            if (client.options.sneakKey.isPressed()) {
                upMult = -1.1;
            }

            vehicle.setVelocity(movementVec.x, upMult, movementVec.z);
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
