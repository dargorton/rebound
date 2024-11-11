package net.backdoor.client.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.backdoor.client.Backdoor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Unique
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(at = @At("HEAD"), method = "init")
    private void init(CallbackInfo ci) {
        mc.getWindow().setTitle("Backdoor " + Backdoor.VERSION);
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.drawText(mc.textRenderer, "Backdoor Client - Release " + Backdoor.VERSION, 4, 4, new Color(133, 62, 223).getRGB(), true);
    }
}
