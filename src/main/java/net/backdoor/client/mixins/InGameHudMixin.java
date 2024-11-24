package net.backdoor.client.mixins;

import java.awt.Color;
import net.backdoor.client.Backdoor;
import net.backdoor.client.mods.Module;
import net.backdoor.client.manager.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    public void renderCustomText(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            String text = "Backdoor " + Backdoor.VERSION;
            boolean shadow = true;
            float hueStep = 0.05F;
            float baseHue = (float) (System.currentTimeMillis() % 10000L) / 10000.0F;
            float xPos = 4.0F;
            float yPos = 4.0F;

            for (int i = 0; i < text.length(); ++i) {
                float hue = (baseHue + (float) i * hueStep) % 1.0F;
                Color color = Color.getHSBColor(hue, 1.0F, 1.0F);
                int rgbColor = color.getRGB() & 0xFFFFFF;
                String character = String.valueOf(text.charAt(i));
                context.drawText(client.textRenderer, character, (int) xPos, (int) yPos, rgbColor, shadow);
                xPos += client.textRenderer.getWidth(character);
            }

            String fpsText = "FPS: " + client.getCurrentFps();
            xPos = 4.0F;
            float fpsYPos = yPos + 10.0F;

            // Render FPS
            for (int i = 0; i < fpsText.length(); ++i) {
                float hue = (baseHue + (float) i * hueStep) % 1.0F;
                Color color = Color.getHSBColor(hue, 1.0F, 1.0F);
                int rgbColor = color.getRGB() & 0xFFFFFF;
                String character = String.valueOf(fpsText.charAt(i));
                context.drawText(client.textRenderer, character, (int) xPos, (int) fpsYPos, rgbColor, shadow);
                xPos += client.textRenderer.getWidth(character);
            }

            int screenWidth = client.getWindow().getScaledWidth();
            float moduleXPos = screenWidth - 4.0F;
            float moduleYPos = 4.0F;

            List<Module> enabledModules = ModuleManager.list.stream()
                    .filter(Module::getEnabled)
                    .sorted((m1, m2) -> Integer.compare(m2.getName().length(), m1.getName().length())) // Sort by name length
                    .toList();

            for (Module module : enabledModules) {
                String moduleName = module.getName();
                float moduleNameWidth = client.textRenderer.getWidth(moduleName);

                moduleXPos -= moduleNameWidth;

                for (int i = 0; i < moduleName.length(); ++i) {
                    float hue = (baseHue + (float) i * hueStep) % 1.0F;
                    Color color = Color.getHSBColor(hue, 1.0F, 1.0F);
                    int rgbColor = color.getRGB() & 0xFFFFFF;
                    String character = String.valueOf(moduleName.charAt(i));
                    context.drawText(client.textRenderer, character, (int) moduleXPos, (int) moduleYPos, rgbColor, shadow);
                    moduleXPos += client.textRenderer.getWidth(character);
                }

                moduleXPos = screenWidth - 4.0F;
                moduleYPos += 10.0F;
            }
        }
    }
}

