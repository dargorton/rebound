package net.backdoor.client.mods.movement;

import net.backdoor.client.devutil.TickDelayHandler;
import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;
import net.backdoor.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.function.ToDoubleBiFunction;

public class Strafe extends Module {


    public Strafe() {
        super("Strafe", Category.MOVEMENT, new ArrayList<>());
    }

    public void onTick(MinecraftClient client) {
        Vec3d movementVec = Vec3d.ZERO;
        assert client.player != null;
        Vec3d lookVec = client.player.getRotationVec(1.0F);

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
            if (client.options.leftKey.isPressed()) {
                movementVec = movementVec.add(lookVec.rotateY(90).multiply(1.0));
            }
            /* ------------ */

            TickDelayHandler.runAfterTicks(() -> {
                assert p != null;
                p.jump();
            }, 20);

            if (!movementVec.equals(Vec3d.ZERO)) {
                p.setVelocity(movementVec.x, p.getVelocity().y, movementVec.z);
            }
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
