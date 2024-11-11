package net.backdoor.client.util;

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
}
