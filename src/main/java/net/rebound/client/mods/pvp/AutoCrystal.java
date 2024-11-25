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
public class AutoCrystal extends Module {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<LivingEntity> targets = new ArrayList<>();
    private final BlockPos.Mutable placingCrystalBlockPos = new BlockPos.Mutable();
    private final ArrayList<Entity> placedCrystals = new ArrayList<>();
    private boolean canPlace = true;
    private boolean canBreak = true;

    public static Setting<Integer> range = new Setting<>("Range",
            "range", 5,
            null,
            null,
            null);
    public static Setting<Integer> hitDelay = new Setting<>("Hit Delay",
            "Delay before hitting crystals", 0,
            null, null, null);

    public static Setting<Integer> placeDelay = new Setting<>("Place Delay",
            "Delay before placing crystals", 0,
            null, null, null);

    public AutoCrystal() {
        super("AutoCrystal", Category.PVP, new ArrayList<>());
        this.settings.add(range);
        this.settings.add(hitDelay);
        this.settings.add(placeDelay);
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
        if (canPlace || !isEndCrystalAtPos(placingCrystalBlockPos)) {
            canPlace = false;

            // Delay placement using TickDelayHandler
            TickDelayHandler.runAfterTicks(() -> {
                assert client.player != null;
                BlockPos placePos = doPlace(client.player);
                if (placePos != null) {
                    placingCrystalBlockPos.set(placePos).move(0, 1, 0);
                }
                canPlace = true;
            }, placeDelay.getValue() * 10); // Convert scale to ticks
        }
    }

    private void handleBreakAction() {
        if (!placedCrystals.isEmpty()) {
            for (Entity crystal : placedCrystals) {
                if (crystal != null) {
                    canBreak = false;

                    // Delay hitting using TickDelayHandler
                    TickDelayHandler.runAfterTicks(() -> {
                        hitCrystal(crystal);
                        canBreak = true;
                    }, hitDelay.getValue() * 10); // Convert scale to ticks
                }
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
        ItemStack offHand = player.getOffHandStack();
        if (heldItem.getItem() != Items.END_CRYSTAL) {
            return null;
        }

        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        BlockPos closestPos = null;
        BlockPos placedPos = null;
        double closestDistance = Double.MAX_VALUE;

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

        // Find lethal positions
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

        // If no lethal crystal spot was found, check nearby positions
        if (closestPos == null) {
            for (int x = -5; x <= 5; x++) {
                for (int y = -5; y <= 5; y++) {
                    for (int z = -5; z <= 5; z++) {
                        blockPos.set(targetPos.getX() + x, targetPos.getY() + y, targetPos.getZ() + z);
                        assert client.world != null;
                        Block block = client.world.getBlockState(blockPos).getBlock();

                        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
                            blockPos.set(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
                            if (!client.world.getBlockState(blockPos).isAir()) continue;
                            if (BlockUtil.isBoundingBoxIntersectingBlock(target, blockPos)) continue;

                            blockPos.set(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ());

                            if (BlockUtil.isBoundingBoxIntersectingEntityType(EntityType.END_CRYSTAL, blockPos)) continue;

                            double distance = targetPos.getSquaredDistance(blockPos);
                            double range = player.getBlockPos().up().getSquaredDistance(blockPos);
                            if (distance < closestDistance) {
                                if (Math.sqrt(range) < (double) this.range.getValue()) {
                                    closestDistance = distance;
                                    closestPos = blockPos.toImmutable();
                                }
                            }
                        }
                    }
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

            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0)); // place

            client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND)); // swing

            assert client.player != null;
            client.player.swingHand(Hand.MAIN_HAND);

            placedPos = closestPos;

            // Find the closest crystal to hit
            Entity closestCrystal = findClosestCrystalToPlayer(player);
            if (closestCrystal != null) {
                hitCrystal(closestCrystal); // Attack the crystal
            }
        }
        return placedPos;
    }

    // New method to find the closest crystal
    private Entity findClosestCrystalToPlayer(PlayerEntity player) {
        double closestDistance = Double.MAX_VALUE;
        Entity closestCrystal = null;

        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                double distance = player.squaredDistanceTo(entity);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestCrystal = entity;
                }
            }
        }

        return closestCrystal;
    }

    private void hitCrystal(Entity crystal) {
        if (crystal.getType() == EntityType.END_CRYSTAL) {
            try {
                assert client.interactionManager != null;
                client.interactionManager.attackEntity(client.player, crystal);
            } catch (Exception ignored) {
            }
        }
    }

    private void findTargets() {
        targets.clear();
        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if (entity.getType() == EntityType.PLAYER || entity.getType() == EntityType.WITHER ) {
                if (entity != client.player) {
                    targets.add((LivingEntity) entity);
                }
            }
        }
    }

    private LivingEntity getNearestTarget() {
        double closestDistance = Double.MAX_VALUE;
        LivingEntity closestTarget = null;

        for (LivingEntity target : targets) {
            assert client.player != null;
            double distance = client.player.squaredDistanceTo(target);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = target;
            }
        }

        return closestTarget;
    }

    private boolean isEndCrystalAtPos(BlockPos pos) {
        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                if (entity.getBlockPos().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onCrystalAdded(EndCrystalEntity crystal) {
        // This method is called when a new crystal is placed.
        placedCrystals.add(crystal);
    }

    public void onCrystalRemoved(EndCrystalEntity crystal) {
        // This method is called when a crystal is removed (e.g., destroyed).
        placedCrystals.remove(crystal);
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}