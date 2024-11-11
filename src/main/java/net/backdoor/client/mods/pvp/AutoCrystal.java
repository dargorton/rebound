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
import net.backdoor.client.mods.Module;
import net.backdoor.client.setting.Setting;
import net.backdoor.client.devutil.BlockUtil;
import net.backdoor.client.devutil.MathUtil;
import net.backdoor.client.devutil.RotateUtil;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class AutoCrystal extends Module {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<LivingEntity> targets = new ArrayList<>();

    private final BlockPos.Mutable placingCrystalBlockPos = new BlockPos.Mutable();

    private final ArrayList<Entity> placedCrystals = new ArrayList<>();

    private boolean canPlace = true;





    public static Setting<Integer> range = new Setting<>("range", "range", 5, null, null, null);



    public AutoCrystal() {
        super("AutoCrystal", Category.PVP, new ArrayList<>());
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

            if (canPlace || !isEndCrystalAtPos(placingCrystalBlockPos)) {
                canPlace = false;
                TickDelayHandler.runAfterTicks(() -> {
                    BlockPos impliedPlacePos = doPlace(client.player);
                    if (impliedPlacePos != null) {
                        placingCrystalBlockPos.set(impliedPlacePos).move(0, 1, 0);
                    }  /*else {//lets try again
                        canPlace = true;
                        // cant place! we're fucked!
                    }*/

                }, 2);
            }

            /*if (canBreak) {
                canBreak = false;
                TickDelayHandler.runAfterTicks(() -> {
                    if (!placedCrystals.isEmpty()) {
                        for (Entity crystal : placedCrystals) {
                            if (crystal != null)
                                doBreak(crystal);
                        }
                    }
                    canBreak = true;
                    canPlace = true;
                }, 10);
            }*/
                //lets attack now


        } else {
            canPlace = true;
            placedCrystals.clear();
        }
    }

    public void onCrystalRemoved() {
        if (!placedCrystals.isEmpty()) {
            placedCrystals.removeLast();
        }
    }


    public void onCrystalAdded(Entity crystal) {
        if (!enabled) return;
        if (crystal.getBlockPos().equals(placingCrystalBlockPos)) {
            placedCrystals.add(crystal);
            for (Entity placedCrystal : placedCrystals) {
                TickDelayHandler.runAfterTicks(() -> doBreak(placedCrystal), 3);
            }
        }
        //canPlace = true;
    }


    private BlockPos doPlace (PlayerEntity player) {
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
        BlockPos targetPos = getNearestTarget().getBlockPos();


        //we are gonna find lethals first.

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

            if (BlockUtil.isBoundingBoxIntersectingEntityType(EntityType.END_CRYSTAL, pos.up())) {continue;} // does it intersect other crystals

            // Check if the block is obsidian or bedrock, and if the block above is air
            if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && blockAbove == Blocks.AIR) {
                closestPos = pos;
            }
        }


        if (closestPos == null) { // then no lethal crystal spot
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

                            blockPos.set(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ());// uhh go back

                            if (BlockUtil.isBoundingBoxIntersectingEntityType(EntityType.END_CRYSTAL, blockPos)) {continue;} // does it intersect other crystals



                            double distance = targetPos.getSquaredDistance(blockPos);
                            double range = player.getBlockPos().up().getSquaredDistance(blockPos);
                            if (distance < closestDistance && range < 5.0) {

                                closestDistance = distance;
                                closestPos = blockPos.toImmutable();
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

            client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));// swing

            assert client.player != null;
            client.player.swingHand(Hand.MAIN_HAND);


            placedPos = closestPos;
        }
        return placedPos;
    }

    private void doBreak (Entity crystal) {
        if (crystal.getType() == EntityType.END_CRYSTAL) {
            assert Backdoor.mc.player != null;
            double range = Backdoor.mc.player.getBlockPos().up().getSquaredDistance(crystal.getBlockPos());
            System.out.println(range);
            if (range < 5.0) return;
            //RotateUtil.rotatePlayerToFace(placedPos, false);
            assert client.player != null;
            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, client.player.isSneaking()));
            client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            client.player.swingHand(Hand.MAIN_HAND);
            if (canPlace) {
                placedCrystals.remove(crystal);
            }
            //canPlace = true;
        }
    }

    private boolean isEndCrystalAtPos(BlockPos pos) {
        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if (entity.getType() == EntityType.END_CRYSTAL) {
                if (entity.getBlockPos().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeSpecificCrystal (BlockPos pos) {
        RotateUtil.rotatePlayerToFace(pos, false);

        BlockHitResult blockHitResult = new BlockHitResult(
                new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5),
                Direction.UP,
                pos,
                false
        );

        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0)); // place

        client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));// swing

        assert client.player != null;
        client.player.swingHand(Hand.MAIN_HAND);


    }



    private void findTargets() {

        targets.clear();

        // Living Entities
        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            // Ignore non-living
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            // Player
            if (livingEntity instanceof PlayerEntity enemy) {
                if (enemy.getAbilities().creativeMode || livingEntity == client.player) continue;
                if (!enemy.isAlive()) continue;

            }

            // Animals, water animals, monsters, bats, misc
            //if (!(entities.get().contains(livingEntity.getType()))) continue;
            if (!(livingEntity.getType().equals(EntityType.PLAYER))) continue; // is a player

            // Close enough to damage                           target range
            if (livingEntity.squaredDistanceTo(client.player) > 8 * 8) continue;

            targets.add(livingEntity);
        }
    }

    private LivingEntity getNearestTarget() {
        LivingEntity nearestTarget = null;
        double nearestDistance = Double.MAX_VALUE;

        for (LivingEntity target : targets) {
            double distance = MathUtil.squaredDistanceTo(target);

            if (distance < nearestDistance) {
                nearestTarget = target;
                nearestDistance = distance;
            }
        }

        return nearestTarget;
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}