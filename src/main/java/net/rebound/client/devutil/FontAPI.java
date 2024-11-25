package net.rebound.client.devutil;

import me.x150.renderer.font.FontRenderer;

import java.awt.*;

public class FontAPI {
    public static Font loadTTF(String name) {
        try {
            var a = FontAPI.class.getClassLoader().getResourceAsStream(name);
            if (a == null) {
                System.err.println("Error: Font resource '" + name + "' not found.");
                return null;
            }
            return Font.createFont(Font.TRUETYPE_FONT, a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static final Font arial = loadTTF("opensans-light.ttf");
    public final static FontRenderer OSANS = arial != null ? new FontRenderer(new Font[]{arial}, 9f) : null;

    /**
     * removed for uselessness
    private static Font jbmono = new Font("Jetbrains Mono", Font.PLAIN, 18);
    public final static FontRenderer MONO = new FontRenderer(new Font[]{jbmono},9f);
    */
}