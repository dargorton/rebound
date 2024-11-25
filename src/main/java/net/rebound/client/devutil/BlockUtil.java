package net.rebound.client.devutil;

import net.rebound.client.Rebound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BlockUtil {

    public static boolean isBoundingBoxIntersectingBlock(LivingEntity entity, BlockPos blockPos) {
        if (entity == null || blockPos == null) {
            return false;
        }

        // Get the bounding box of the entity
        Box entityBox = entity.getBoundingBox();

        // Create a bounding box for the block at blockPos
        Box blockBox = new Box(blockPos);

        // Check if the two boxes intersect
        return entityBox.intersects(blockBox);
    }

    /**
     * This returns if any entity under the specified LivingEntity intersects the blockpos
     * @param entityType
     * @param blockPos
     * @return
     */
    public static boolean isBoundingBoxIntersectingEntityType(EntityType entityType, BlockPos blockPos) {
        if (entityType == null || blockPos == null) {
            return false;

        }
        Box blockBox;
        if (entityType.equals(EntityType.END_CRYSTAL)) {
            blockBox = new Box(
                    blockPos.getX() - 0.5, blockPos.getY() - 0.5, blockPos.getZ() - 0.5,
                    blockPos.getX() + 1.5, blockPos.getY() + 1.5, blockPos.getZ() + 1.5
            );
        } else {
            blockBox = new Box(blockPos);
        }


        for (Entity worldEnt : Rebound.mc.world.getEntities()) {
            if (worldEnt.getType() == entityType) {
                if (worldEnt.getBoundingBox().intersects(blockBox)) {
                    return true;
                }
            }
        }
        return false;


    }
}
