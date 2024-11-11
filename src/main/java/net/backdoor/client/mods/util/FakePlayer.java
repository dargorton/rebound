package net.backdoor.client.mods.util;

import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;
import net.backdoor.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.backdoor.client.devutil.FakePlayerEntity;

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
