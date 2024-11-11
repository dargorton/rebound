package net.backdoor.client.mods.pvp;

import net.backdoor.client.Backdoor;
import net.backdoor.client.devutil.TickDelayHandler;
import net.backdoor.client.mods.Category;
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
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.backdoor.client.mods.Module;
import net.backdoor.client.setting.Setting;
import net.backdoor.client.devutil.BlockUtil;
import net.backdoor.client.devutil.RotateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class AutoCrystalRework extends Module {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<LivingEntity> targets = new ArrayList<>();
    private final BlockPos.Mutable placingCrystalBlockPos = new BlockPos.Mutable();
    private boolean canPlace = true;
    private boolean canBreak = true;

    private BlockPos lethalPlacementPos = null;  // Store the lethal placement position

    public static Setting<Integer> range = new Setting<>("range", "range", 5, null, null, null);

    public AutoCrystalRework() {
        super("AutoCrystalRework", Category.PVP, new ArrayList<>());
        this.settings.add(range);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        TickDelayHandler.init();
    }

    public void onTick(MinecraftClient client) {

        if (client == null || client.player == null) {
            return;
        }

        if (client.player.getMainHandStack().getItem() != Items.END_CRYSTAL ||
            client.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
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
        if (canPlace && lethalPlacementPos != null) {
            canPlace = false;

            // Repeatedly place the crystal on the same lethal position
            BlockPos placedPos = doPlace(client.player, lethalPlacementPos);
            if (placedPos != null) {
                lethalPlacementPos = placedPos;  // Keep using this spot until conditions change
            }

            canPlace = true;
        }
    }

    private void handleBreakAction() {
        if (!canBreak) return;

        if (lethalPlacementPos != null) {
            // Look for any placed crystal on the lethal spot
            Entity crystal = findPlacedCrystalAt(lethalPlacementPos);
            if (crystal != null) {
                doBreak(crystal);  // Break the crystal
            }
        }

        canBreak = true;
    }

    private void reset() {
        canPlace = true;
        canBreak = true;
        lethalPlacementPos = null;  // Reset lethal placement position
    }

    private BlockPos doPlace(PlayerEntity player, BlockPos lethalSpot) {
        ItemStack heldItem = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        if (heldItem.getItem() != Items.END_CRYSTAL) {
            return null;
        }

        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        BlockPos placedPos = null;

        // Place the crystal repeatedly on the lethal spot
        assert client.world != null;
        Block block = client.world.getBlockState(lethalSpot).getBlock();
        Block blockAbove = client.world.getBlockState(lethalSpot.up()).getBlock();

        // Check if the lethal spot is still valid for placement
        if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && blockAbove == Blocks.AIR) {
            RotateUtil.rotatePlayerToFace(lethalSpot, false);

            BlockHitResult blockHitResult = new BlockHitResult(
                    new Vec3d(lethalSpot.getX() + 0.5, lethalSpot.getY() + 1.0, lethalSpot.getZ() + 0.5),
                    Direction.UP,
                    lethalSpot,
                    false
            );

            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0)); // place
            client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND)); // swing

            assert client.player != null;
            client.player.swingHand(Hand.MAIN_HAND);

            placedPos = lethalSpot;
        }
        return placedPos;
    }

    // Find an EndCrystalEntity that has been placed at the lethal spot
    private Entity findPlacedCrystalAt(BlockPos lethalSpot) {
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                if (entity.getBlockPos().equals(lethalSpot)) {
                    return entity;
                }
            }
        }
        return null;
    }

    private void doBreak(Entity crystal) {
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
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity != client.player) {
                    targets.add(livingEntity);
                }
            }
        }
    }

    private LivingEntity getNearestTarget() {
        double closestDistance = Double.MAX_VALUE;
        LivingEntity closestTarget = null;

        for (LivingEntity target : targets) {
            double distance = client.player.squaredDistanceTo(target);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = target;
            }
        }

        return closestTarget;
    }

    private boolean isEndCrystalAtPos(BlockPos pos) {
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                if (entity.getBlockPos().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Main logic for deciding where to place the lethal crystal spot
    private void setLethalSpot(LivingEntity target) {
        BlockPos targetPos = target.getBlockPos();

        // Example logic for determining the lethal spot:
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

            if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && blockAbove == Blocks.AIR) {
                lethalPlacementPos = pos;  // Set the lethal spot
                break;
            }
        }
    }

    private void findAndSetTargetLethalSpot() {
        if (client.player != null) {
            LivingEntity nearestTarget = getNearestTarget();
            if (nearestTarget != null) {
                setLethalSpot(nearestTarget);  // Set lethal spot based on nearest target
            }
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
