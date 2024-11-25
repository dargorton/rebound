package net.rebound.client.devutil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TextUtil {
    /*prerequisites*/

    private static int getRainbowColor(float time) {
        float hue = (time + 0.5f) % 1.0f;
        return MathHelper.hsvToRgb(hue, 1.0f, 1.0f) & 0xFFFFFF; // Get RGB color
    }
    /*utils*/
    public static void drawRainbowText(String args, float x, float y) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            MatrixStack matrices = new MatrixStack();
            TextRenderer textRenderer = client.textRenderer;

            float time = (System.currentTimeMillis() % 1000) / 1000f;
            int color = getRainbowColor(time);

            textRenderer.draw(
                    Text.of(args),
                    x,
                    y,
                    color,
                    true,
                    matrices.peek().getPositionMatrix(),
                    client.getBufferBuilders().getEntityVertexConsumers(),
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    0
            );
        }
    }
}
