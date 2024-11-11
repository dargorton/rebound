package net.backdoor.client.util;

import net.backdoor.client.Backdoor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RotateUtil {

    private static final List<Rotation> rotations = new ArrayList<>();

    public static float serverYaw;
    public static float serverPitch;

    public static float preYaw;

    public static float prePitch;

    public static void rotatePlayerToFace(BlockPos targetPos, boolean isClient) { // wtf
        Vec3d playerPos = Backdoor.mc.player.getPos().add(0, Backdoor.mc.player.getEyeHeight(Backdoor.mc.player.getPose()), 0); // Player's eye position
        Vec3d targetVec = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5); // Center of the block

        Vec3d direction = targetVec.subtract(playerPos).normalize(); // Calculate the direction vector

        // Calculate yaw and pitch to rotate the player
        double yaw = (double) (org.joml.Math.atan2(direction.z, direction.x) * (180 / org.joml.Math.PI)) - 90;
        double pitch = (double) (-org.joml.Math.atan2(direction.y, org.joml.Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180 / org.joml.Math.PI));

        // Apply the rotation to the player's camera
        queueRotation(yaw,pitch,0,isClient,null);
        //Backdoor.mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, Backdoor.mc.player.isOnGround()));

    }


    private static void queueRotation(double yaw, double pitch, int priority, boolean clientSide, Runnable callback) {
        Rotation rotation = new Rotation();
        rotation.set(yaw, pitch, priority, clientSide, callback);

        int i = 0;
        for (; i < rotations.size(); i++) {
            if (priority > rotations.get(i).priority) break;
        }

        rotations.add(i, rotation);
    }

    public static void queueRotation(double yaw, double pitch) {
        queueRotation(yaw, pitch, 0,  false, null);
    }



    public static void onSendMovementPackets() {
        if (!rotations.isEmpty()) {
            for (int i = 0; i < rotations.size(); i++) {
                Rotation rotation = rotations.get(i);

                if (rotation.clientSide) {
                    setClientRotation((float) rotation.yaw, (float) rotation.pitch);
                } else {
                    rotation.sendPacket();
                }
            }
        }
    }


    private static void setClientRotation(float yaw, float pitch) {
        preYaw = Backdoor.mc.player.getYaw();
        prePitch = Backdoor.mc.player.getPitch();

        Backdoor.mc.player.setYaw(yaw);
        Backdoor.mc.player.setPitch(pitch);
    }

    public static void setServerRotation(double yaw, double pitch) {
        preYaw = Backdoor.mc.player.getYaw();
        prePitch = Backdoor.mc.player.getPitch();

        Backdoor.mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, Backdoor.mc.player.isOnGround()));
    }

    private static class Rotation {
        public double yaw, pitch;
        public int priority;
        public boolean clientSide;
        public Runnable callback;

        public void set(double yaw, double pitch, int priority, boolean clientSide, Runnable callback) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.priority = priority;
            this.clientSide = clientSide;
            this.callback = callback;
        }

        public void sendPacket() {
            Backdoor.mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, Backdoor.mc.player.isOnGround()));
            runCallback();
        }

        public void runCallback() {
            if (callback != null) callback.run();
        }
    }

}
