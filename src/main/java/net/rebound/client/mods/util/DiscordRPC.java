package net.rebound.client.mods.util;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.MinecraftClient;
import net.rebound.client.Rebound;

import java.time.Instant;
import java.util.Objects;

public class DiscordRPC {
    private static Core core;
    private static Thread rpcThread;
    private static boolean running = false;

    public static void startRPC() {
        if (core != null || running) return; // Prevent multiple instances

        try {
            CreateParams params = new CreateParams();
            params.setClientID(1305283934036168744L);
            params.setFlags(CreateParams.getDefaultFlags());
            core = new Core(params);

            running = true;

            // Start a new thread to keep updating the RPC
            rpcThread = new Thread(() -> {
                while (running) {
                    try {
                        updateActivity();
                        core.runCallbacks(); // Required to keep the connection alive
                        Thread.sleep(5000); // Update every 5 seconds
                    } catch (Exception e) {
                        System.err.println("Error in Discord RPC thread: " + e.getMessage());
                        e.printStackTrace();
                        stopRPC();
                    }
                }
            });
            rpcThread.setDaemon(true);
            rpcThread.start();

        } catch (Exception e) {
            System.err.println("Failed to initialize Discord RPC: " + e.getMessage());
            e.printStackTrace();
            stopRPC();
        }
    }

    public static void stopRPC() {
        running = false;

        if (rpcThread != null && rpcThread.isAlive()) {
            try {
                rpcThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (core != null) {
            core.close();
            core = null;
        }
    }

    private static void updateActivity() {
        if (core == null || !core.isDiscordRunning()) {
            stopRPC();
            return;
        }

        try (Activity activity = new Activity()) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.world == null) {
                activity.setState("Idling");
            } else if (client.isInSingleplayer()) {
                activity.setState("Playing Singleplayer");
            } else if (client.getNetworkHandler() != null && client.getNetworkHandler().getServerInfo() != null) {
                String ip = Objects.requireNonNull(client.getNetworkHandler().getServerInfo().address);
                activity.setState("Playing on " + ip);
            }

            activity.setDetails("Rebound Client " + Rebound.VERSION);
            activity.timestamps().setStart(Instant.now());
            activity.assets().setLargeImage("rebound");
            activity.assets().setSmallImage("minecraftlogo");

            core.activityManager().updateActivity(activity);
        } catch (Exception e) {
            System.err.println("Failed to update Discord activity: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
