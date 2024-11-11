package net.backdoor.client.mods.movement;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;

import java.util.ArrayList;
import java.util.Objects;

import static net.backdoor.client.Backdoor.mc;

public class ElytraBounce extends Module {

    public ElytraBounce() {
        super("ElytraBounce", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (client.player == null || !this.enabled) return;

        ClientPlayerEntity player = client.player;
        if (player.isFallFlying() && this.enabled) {
            adjustYaw(player);
            moveForwardAndJump(player);
        }
    }

    private void adjustYaw(ClientPlayerEntity player) {
        // Lower the yaw slightly for better highway movement control
        float bounceYaw = player.getYaw() - 2.0F;  // Decrease yaw to move the player faster down the highway
        player.setYaw(bounceYaw);
    }

    private void moveForwardAndJump(ClientPlayerEntity player) {
        Vec3d velocity = player.getVelocity();

        // Ensure the player is moving forward
        mc.options.forwardKey.setPressed(true);  // Simulate the W key press (forward)

        // Apply a small upward velocity to simulate the jump effect for the Elytra
        player.setVelocity(velocity.x, 0.05D, velocity.z);  // Slight upward movement for smooth fall

        // Send the movement packet to the server with the updated player position and velocity
        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(),
                player.getY(),
                player.getZ(),
                player.isOnGround());
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);  // Send the packet to update the player position on the server
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;  // Toggle the module state
    }
}
