package net.rebound.client.mods.util;

import net.rebound.client.mods.Category;
import net.rebound.client.mods.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.rebound.client.devutil.FakePlayerEntity;

import java.util.ArrayList;

public class FakePlayer extends Module {
    public FakePlayer() {
        super("FakePlayer", Category.MISC, new ArrayList<>());
    }

    public void onTick(MinecraftClient client) {

        PlayerEntity p = client.player;
        FakePlayerEntity fakePlayer = new FakePlayerEntity("FakePlayer");

        if (enabled) {

            assert client.interactionManager != null;
            assert client.player != null;
            fakePlayer.spawn();
        } else fakePlayer.despawn();
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
