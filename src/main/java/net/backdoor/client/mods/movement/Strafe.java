package net.backdoor.client.mods.movement;

import net.backdoor.client.devutil.TickDelayHandler;
import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.backdoor.client.Backdoor.mc;

public class Strafe extends Module {

    public Strafe() {
        super("Strafe", Category.MOVEMENT, new ArrayList<>());
    }

    public void onTick(MinecraftClient client) {
        Vec3d movementVec = Vec3d.ZERO;
        assert client.player != null;
        Vec3d lookVec = client.player.getRotationVec(1.0F);
        ClientPlayerInteractionManager in = client.interactionManager;

        client.player.setSprinting(true);
        if (this.enabled) {
            /* ------------ */
            PlayerEntity p = client.player;
            /* ------------ */
            if (client.options.forwardKey.isPressed())
                movementVec = movementVec.add(lookVec.multiply(1.0));
            if (client.options.backKey.isPressed())
                movementVec = movementVec.add(lookVec.multiply(-1).multiply(1.0));
            if (client.options.rightKey.isPressed())
                movementVec = movementVec.add(lookVec.rotateY(-90).multiply(1.0));
            if (client.options.leftKey.isPressed())
                movementVec = movementVec.add(lookVec.rotateY(90).multiply(1.0));
            /* ------------ */

            if (!movementVec.equals(Vec3d.ZERO) || p != null || client != null) {
                TickDelayHandler.runAfterTicks(() -> {
                    assert p != null;
                    if (p.isOnGround() && p.isSprinting()) {
                        client.options.jumpKey.setPressed(true);
                    }
                }, 20);
                TickDelayHandler.init();
                assert p != null;
                p.setVelocity(movementVec.x, p.getVelocity().y, movementVec.z); // Keep the existing Y velocity
            }
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
