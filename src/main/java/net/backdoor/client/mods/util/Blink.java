package net.backdoor.client.mods.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.backdoor.client.Backdoor.mc;

public class Blink extends Module {
    private final List<PlayerMoveC2SPacket> packets = new LinkedList<>();
    private boolean isBlinking = false;
    private int timer = 0;

    public Blink() {
        super("Blink", Category.MISC, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }

    private void onStartBlink() {
        if (mc.player != null) {
            isBlinking = true;
            packets.clear();
            timer = 0;
        }
    }

    private void onStopBlink() {
        if (isBlinking) {
            sendStoredPackets();
            isBlinking = false;
        }
    }

    private void onTick(MinecraftClient client) {
        if (mc.player == null || !this.enabled) return;

        timer++;


        if (this.enabled) {
            onStartBlink();
        } else {
            onStopBlink();
        }
    }

    public void onSendPacket(PlayerMoveC2SPacket packet) {
        if (!this.enabled || !(packet instanceof PlayerMoveC2SPacket)) return;

        // Cancel sending packets to create the "Blink" effect
        synchronized (packets) {
            packets.add(packet);
        }
    }

    private void sendStoredPackets() {
        synchronized (packets) {
            if (mc.getNetworkHandler() != null) {
                for (PlayerMoveC2SPacket packet : packets) {
                    mc.getNetworkHandler().sendPacket(packet);
                }
            }
            packets.clear();
        }
    }
}
