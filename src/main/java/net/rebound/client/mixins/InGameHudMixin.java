package net.rebound.client.mixins;

import net.rebound.client.Rebound;
import net.rebound.client.devutil.FontAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    public void renderCustomText(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {

            if (Objects.requireNonNull(
                    client.getNetworkHandler()).getPlayerListEntry(client.player.getUuid()) == null
            ||      client.getNetworkHandler().getPlayerList() == null
) return;
            // Render Backdoor Version
            String versionText = "Rebound " + Rebound.VERSION;
            context.getMatrices().push();
            FontAPI.OSANS.drawString(
                    context.getMatrices(),
                    versionText,
                    4 + 5, // Adjust x position
                    4 + 2, // Adjust y position
                    1, 1, 1, 1
            );
            context.getMatrices().pop();

            // Render FPS
            String fpsText = "FPS: " + client.getCurrentFps();
            context.getMatrices().push();
            FontAPI.OSANS.drawString(
                    context.getMatrices(),
                    fpsText,
                    4 + 5, // Adjust x position
                    14 + 2, // Adjust y position (10px below version text)
                    1, 1, 1, 1
            );
            context.getMatrices().pop();

            // Render Ping
            int ping = client.getNetworkHandler() != null && client.getNetworkHandler().getPlayerListEntry(client.player.getUuid()) != null
                    ? Objects.requireNonNull(client.getNetworkHandler().getPlayerListEntry(client.player.getUuid())).getLatency()
                    : -1;

            String pingText = "Ping: " + (ping >= 0 ? ping + "ms" : "N/A");
            context.getMatrices().push();
            FontAPI.OSANS.drawString(
                    context.getMatrices(),
                    pingText,
                    4 + 5, // Adjust x position
                    24 + 2, // Adjust y position (10px below FPS text)
                    1, 1, 1, 1
            );
            context.getMatrices().pop();
        }
    }
}
