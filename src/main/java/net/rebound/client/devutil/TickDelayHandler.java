package net.rebound.client.devutil;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.HashSet;
import java.util.Set;

public class TickDelayHandler {
    private static final Set<DelayedTask> delayedTasks = new HashSet<>();

    public static void init() {
        // Register a tick event to process delayed tasks
        ClientTickEvents.END_CLIENT_TICK.register(TickDelayHandler::onClientTick);
    }

    public static void runAfterTicks(Runnable action, int ticks) {
        delayedTasks.add(new DelayedTask(action, ticks));
    }

    private static void onClientTick(MinecraftClient client) {
        // Process each task, decrementing its tick counter
        delayedTasks.removeIf(task -> {
            task.ticksRemaining--;
            if (task.ticksRemaining <= 0) {
                task.action.run();
                return true; // Remove task after running
            }
            return false;
        });
    }

    // Inner class to keep track of each delayed task
    private static class DelayedTask {
        private final Runnable action;
        private int ticksRemaining;

        public DelayedTask(Runnable action, int ticksRemaining) {
            this.action = action;
            this.ticksRemaining = ticksRemaining;
        }
    }
}