package net.backdoor.client.mods.render;

import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;

import static java.awt.Color.*;

public class ESP extends Module {
    MinecraftClient c = MinecraftClient.getInstance();

    public ESP() {
        super("ESP", Category.RENDER, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public void onTick(MinecraftClient m) {
        if (enabled) {
         // do nothing //   renderEntities();
        }
    }
/*
    private void renderEntities() {
        if (c.world == null || c.player == null) return;

        c.world.getEntities().forEach(entity -> {
            if (entity instanceof ItemEntity) {
                return;
            }
            drawOutline(new MatrixStack(), entity, red.getRGB(), green.getRGB(), blue.getRGB());
        });
    }
    private void drawOutline(MatrixStack matrices, Entity entity, float red, float green, float blue) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        Box boundingBox = entity.getBoundingBox();

        double minX = boundingBox.minX;
        double minY = boundingBox.minY;
        double minZ = boundingBox.minZ;
        double maxX = boundingBox.maxX;
        double maxY = boundingBox.maxY;
        double maxZ = boundingBox.maxZ;

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) minY, (float) minZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) minY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) maxY, (float) minZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) maxY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) minZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) minZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) maxZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) minY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) minY, (float) maxZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) maxY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) maxY, (float) maxZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) maxZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) minY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) minY, (float) maxZ).color(red, green, blue, 1.0f).next();

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) maxY, (float) minZ).color(red, green, blue, 1.0f).next();
        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float) maxX, (float) maxY, (float) maxZ).color(red, green, blue, 1.0f).next();

        tessellator.draw();
    }
*/
    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}

