package com.avandortools.simplemarket.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class WanderWithinRadiusGoal extends Goal {
    private final PathAwareEntity entity;
    private final double speed;
    private final int radius;
    private final BlockPos anchor;

    public WanderWithinRadiusGoal(PathAwareEntity entity, BlockPos anchor, double speed, int radius) {
        this.entity = entity;
        this.speed = speed;
        this.radius = radius;
        this.anchor = anchor;
    }

    @Override
    public EnumSet<Control> getControls() {
        return EnumSet.of(Control.MOVE);
    }

    @Override
    public boolean canStart() {
        return entity.getNavigation().isIdle() && entity.getRandom().nextFloat() < 0.05f;
    }

    @Override
    public void start() {
        System.out.println("WanderWithinRadiusGoal: start");
        BlockPos target = anchor.add(
                entity.getRandom().nextInt(radius * 2) - radius,
                0,
                entity.getRandom().nextInt(radius * 2) - radius
        );
        if (entity.getWorld().getBlockState(target).isAir()) {
            entity.getNavigation().startMovingTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, speed);
        }
    }

    @Override
    public void stop() {
        System.out.println("WanderWithinRadiusGoal: stop");
        BlockPos target = anchor.add(
                entity.getRandom().nextInt(radius * 2) - radius,
                0,
                entity.getRandom().nextInt(radius * 2) - radius
        );
        if (entity.getWorld().getBlockState(target).isAir()) {
            entity.getNavigation().startMovingTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, speed);
        }
    }
}
