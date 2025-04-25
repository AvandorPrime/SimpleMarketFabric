package com.avandortools.simplemarket.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.avandortools.simplemarket.SimpleMarket.LOGGER;

public class StayNearMarketGoal extends Goal {
    private final MobEntity mob;
    private final BlockPos anchor;
    private final double maxDistance;
    private final double speed;

    public StayNearMarketGoal(MobEntity mob, BlockPos anchor, double maxDistance, double speed) {
        this.mob = mob;
        this.anchor = anchor;
        this.maxDistance = maxDistance;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        if (anchor == null) return false;
        return mob.squaredDistanceTo(Vec3d.ofCenter(anchor)) > maxDistance * maxDistance;
    }

    @Override
    public void start() {
        System.out.println("StayNearMarketGoal: start");
        mob.getNavigation().startMovingTo(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5, speed);
    }

    @Override
    public void stop() {
        System.out.println("StayNearMarketGoal: stop");
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }
}
