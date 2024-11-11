package net.backdoor.client.devutil;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

import static net.backdoor.client.Backdoor.mc;

public class FakePlayerEntity {

    private final String name;
    private final GameProfile gameProfile;
    private final MinecraftClient client = mc;
    private PlayerEntity fakePlayer;

    public FakePlayerEntity(String name) {
        this.name = name;
        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
    }

    public void spawn() {
        assert client.player != null && client.world != null;

        BlockPos playerPos = client.player.getBlockPos();
        fakePlayer = new PlayerEntity(client.world, playerPos, client.player.getYaw(),
                     new GameProfile(UUID.randomUUID(), "FakePlayer")) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
    }
    public void despawn() {
        assert client.player != null && client.world != null;
                fakePlayer.kill();
    }
}
