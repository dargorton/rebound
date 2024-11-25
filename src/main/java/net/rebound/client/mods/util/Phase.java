package net.rebound.client.mods.util;

import net.rebound.client.mods.Category;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.rebound.client.mods.Module;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public class Phase extends Module {
    public Phase() {
        super("Phase", Category.MISC, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient mc) {/*
        if (enabled && mc.player != null) {
            Entity player = mc.player;

            if (player.horizontalCollision) {
                player.noClip = true;
                double offset = 0.05;
                player.setPosition(player.getX() + player.getMovementDirection().getOffsetX() * offset,
                        player.getY(),
                        player.getZ() + player.getMovementDirection().getOffsetZ() * offset);

                Objects.requireNonNull(
                        mc.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                                player.getX(), player.getY(), player.getZ(), player.isOnGround()));
            } else {
                player.noClip = false;
            }
        */}

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
