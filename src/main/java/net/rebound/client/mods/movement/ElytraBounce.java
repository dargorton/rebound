package net.rebound.client.mods.movement;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.rebound.client.mods.Category;
import net.rebound.client.mods.Module;
import net.rebound.client.Rebound;

import java.util.ArrayList;
import java.util.Objects;

public class ElytraBounce extends Module {

    public ElytraBounce() {
        super("ElytraBounce", Category.MOVEMENT, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient client) {
        if (Rebound.mc.player == null || !this.enabled || !Rebound.mc.player.isFallFlying()) {
            return;  // Ensure player is fall flying and module is enabled
        }

        ClientPlayerEntity player = client.player;

        // Debugging output to confirm state
        System.out.println("ElytraBounce enabled: " + this.enabled);
        assert player != null;
        System.out.println("Player is fall flying: " + player.isFallFlying());
        System.out.println("Player Pitch: " + player.getPitch());

        // Only activate when player is fall flying and looking downward (pitch < -10)
        if (player.isFallFlying() && player.getPitch() < -10) {
            moveForward(player);  // Simulate forward movement
        }
    }

    private void moveForward(ClientPlayerEntity player) {
        // Debugging output to confirm movement logic
        System.out.println("Moving forward...");

        // Simulate pressing the forward key (W)
        Rebound.mc.options.forwardKey.setPressed(true);

        // Prevent yaw turning if player is facing forward
        float yaw = player.getYaw();
        System.out.println("Current Yaw: " + yaw);

        // Align the yaw to avoid any sideways turning
        if (yaw < 90 && yaw > -90) {
            player.setYaw(0);  // Reset yaw to 0 to keep the player aligned with the forward direction
        }

        // Optionally, send a movement packet to ensure position is updated on the server
        sendMovementPacket(player);
    }

    private void sendMovementPacket(ClientPlayerEntity player) {
        // Send the packet to update the player position on the server (necessary for some mods)
        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.isOnGround()
        );
        Objects.requireNonNull(Rebound.mc.getNetworkHandler()).sendPacket(packet);  // Send the movement update packet
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;  // Toggle the module state
    }
}
