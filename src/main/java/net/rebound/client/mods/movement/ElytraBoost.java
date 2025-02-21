package net.rebound.client.mods.movement;

import net.rebound.client.mods.Category;
import net.rebound.client.mods.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.rebound.client.Rebound;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ElytraBoost extends Module {


    public ElytraBoost() {
        super("ElytraBoost", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        PlayerEntity player = client.player;
          if (enabled) {
              assert Rebound.mc.player != null;
              if (Rebound.mc.player.isFallFlying()) {
                  Vec3d velocity = player.getVelocity();

                  double speedMultiplier = 1.5;
                  Vec3d forward = Vec3d.fromPolar(0, player.getYaw()).normalize().multiply(client.player.input.movementForward * speedMultiplier);
                  Vec3d strafe = Vec3d.fromPolar(0, player.getYaw() - 90).normalize().multiply(client.player.input.movementSideways * speedMultiplier);
                  Vec3d movement = forward.add(strafe);
                  double newYVelocity;

                  if (client.options.jumpKey.isPressed()) {
                      newYVelocity = 1.0;
                  } else if (client.options.sneakKey.isPressed()) {
                      newYVelocity = -1.0;
                  } else {
                      newYVelocity = velocity.y;
                  }

                  player.setVelocity(movement.x, newYVelocity, movement.z);
              }
          }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
