package net.rebound.client.mods.pvp;

import net.rebound.client.devutil.TickDelayHandler;
import net.rebound.client.mods.Category;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.rebound.client.mods.Module;
import net.rebound.client.setting.Setting;
import net.rebound.client.devutil.BlockUtil;
import net.rebound.client.devutil.RotateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class AutoCrystalRework extends Module {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<LivingEntity> targets = new ArrayList<>();
    private final BlockPos.Mutable placingCrystalBlockPos = new BlockPos.Mutable();
    private final ArrayList<Entity> placedCrystals = new ArrayList<>();
    private boolean canPlace = true;
    private boolean canBreak = true;

    // Settings for delays and range
    public static Setting<Integer> hitDelay = new Setting<>("HitDelay", "Delay between crystal hits", 5, null, null, null); // Reduced default
    public static Setting<Integer> placeDelay = new Setting<>("PlaceDelay", "Delay between crystal placements", 5, null, null, null); // Reduced default
    public static Setting<Integer> range = new Setting<>("Range", "Range", 5, null, null, null);
    private long lastPlaceTime = 0;
    private long lastBreakTime = 0;

    public AutoCrystalRework() {
        super("AutoCrystalStrict", Category.PVP, new ArrayList<>());
        this.settings.add(hitDelay);
        this.settings.add(placeDelay);
        this.settings.add(range);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        TickDelayHandler.init();
    }

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        if (client.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {
            return;
        }

        if (enabled) {
            if (canPlace) {
                handlePlaceAction();
            }

            if (canBreak) {
                handleBreakAction();
            }
        } else {
            reset();
        }
    }

    private void handlePlaceAction() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime >= Math.max(placeDelay.getValue(), 1) * 30L) { // Faster delay (30ms per unit)
            lastPlaceTime = currentTime;

            if (canPlace || !isEndCrystalAtPos(placingCrystalBlockPos)) {
                canPlace = false;

                TickDelayHandler.runAfterTicks(() -> {
                    assert client.player != null;
                    BlockPos placePos = doPlace(client.player);
                    if (placePos != null) {
                        placingCrystalBlockPos.set(placePos).move(0, 1, 0);
                    }
                    canPlace = true;
                }, placeDelay.getValue() * 5); // Shortened tick delay multiplier
            }
        }
    }

    private void handleBreakAction() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBreakTime >= Math.max(hitDelay.getValue(), 1) * 30L) { // Faster delay (30ms per unit)
            lastBreakTime = currentTime;

            Entity nearestCrystal = findClosestCrystalToPlayer(client.player);
            if (nearestCrystal != null) {
                canBreak = false;

                TickDelayHandler.runAfterTicks(() -> {
                    hitCrystal(nearestCrystal);
                    canBreak = true;
                }, hitDelay.getValue() * 5); // Shortened tick delay multiplier
            }
        }
    }

    private void reset() {
        canPlace = true;
        canBreak = true;
        placedCrystals.clear();
    }

    private BlockPos doPlace(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getItem() != Items.END_CRYSTAL) {
            return null;
        }

        BlockPos closestPos = null;

        findTargets();

        if (targets.isEmpty()) {
            placedCrystals.clear();
            return null;
        }

        LivingEntity target = getNearestTarget();
        if (target == null) {
            return null;
        }

        BlockPos targetPos = target.getBlockPos();
        BlockPos[] adjacentPositions = {
                targetPos.north(),
                targetPos.south(),
                targetPos.east(),
                targetPos.west()
        };

        for (BlockPos pos : adjacentPositions) {
            pos = pos.down();
            assert client.world != null;
            Block block = client.world.getBlockState(pos).getBlock();
            Block blockAbove = client.world.getBlockState(pos.up()).getBlock();

            if (BlockUtil.isBoundingBoxIntersectingEntityType(EntityType.END_CRYSTAL, pos.up())) {
                continue;
            }

            if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && blockAbove == Blocks.AIR) {
                double range = player.getBlockPos().up().getSquaredDistance(pos);
                if (Math.sqrt(range) < (double) this.range.getValue()) {
                    closestPos = pos;
                }
            }
        }

        if (closestPos != null) {
            RotateUtil.rotatePlayerToFace(closestPos, false);

            BlockHitResult blockHitResult = new BlockHitResult(
                    new Vec3d(closestPos.getX() + 0.5, closestPos.getY() + 1.0, closestPos.getZ() + 0.5),
                    Direction.UP,
                    closestPos,
                    false
            );

            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0));
            client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            assert client.player != null;
            client.player.swingHand(Hand.MAIN_HAND);

            placingCrystalBlockPos.set(closestPos).move(0, 1, 0);
            return closestPos;
        }
        return null;
    }

    private void hitCrystal(Entity crystal) {
        assert client.player != null;
        assert client.getNetworkHandler() != null;

        assert client.interactionManager != null;
        client.interactionManager.attackEntity(client.player, crystal);
        client.player.swingHand(Hand.MAIN_HAND);
        placedCrystals.remove(crystal);
    }

    private Entity findClosestCrystalToPlayer(PlayerEntity player) {
        double closestDistance = Double.MAX_VALUE;
        Entity closestCrystal = null;

        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity && entity.squaredDistanceTo(player) < closestDistance) {
                closestCrystal = entity;
                closestDistance = entity.squaredDistanceTo(player);
            }
        }

        return closestCrystal;
    }

    private void findTargets() {
        targets.clear();
        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof PlayerEntity && !entity.equals(client.player)) {
                targets.add((PlayerEntity) entity);
            }
        }
    }

    private LivingEntity getNearestTarget() {
        double closestDistance = Double.MAX_VALUE;
        LivingEntity nearestTarget = null;
        assert client.world != null;
        for (LivingEntity entity : targets) {
            assert client.player != null;
            double distance = client.player.getBlockPos().getSquaredDistance(entity.getBlockPos());
            if (distance < closestDistance) {
                closestDistance = distance;
                nearestTarget = entity;
            }
        }
        return nearestTarget;
    }

    private boolean isEndCrystalAtPos(BlockPos pos) {
        assert client.world != null;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity crystal) {
                if (crystal.getBlockPos().equals(pos)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
