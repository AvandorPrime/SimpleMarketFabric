package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import com.avandortools.simplemarket.mixin.MobEntityGoalAccessor;
import com.avandortools.simplemarket.util.AvandorTimeUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Responsible for spawning and despawning market entities based on the time of day and current nearbyt entities
 */
public class AmbientMobSpawner {
    static final int spawnRadius = 32;
    public static void tick(World world, BlockPos pos, ArrayList<MobEntity> villagers){
        if (AvandorTimeUtils.worldIsNight(world)) {
            tryDespawnVillager(world, villagers, 10);
        } else {
            final int crateCount = countNearbyMarketCrates(world, pos);
            trySpawnAmbientMob(world, pos, EntityType.CAT, spawnRadius, 2, 100, villagers);
            trySpawnAmbientMob(world, pos, EntityType.WOLF, spawnRadius, 2, 1000, villagers);
            MobEntity newVillager = trySpawnAmbientMob(world, pos, EntityType.VILLAGER, spawnRadius, (int) Math.ceil(0.5*crateCount), 1, villagers);
            if (newVillager != null) villagers.add(newVillager);
        }
    }

    public static void destroyAllVillagers(ArrayList<MobEntity> villagers) {
        Iterator<MobEntity> iterator = villagers.iterator();
        while (iterator.hasNext()) {
            MobEntity villager = iterator.next();
            if (!villager.isRemoved()) {
                villager.discard();
            }
            iterator.remove();
        }
    }

    private static MobEntity trySpawnAmbientMob(World world, BlockPos pos, EntityType<? extends MobEntity> type, int radius, int maxNearby, int chanceOutOf, ArrayList<MobEntity> villagers) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return null;

        if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, false) == null) return null;

        Predicate<MobEntity> filter = mob -> mob.getType() == type && !mob.isPersistent();

        List<MobEntity> nearby = serverWorld.getEntitiesByClass(
                MobEntity.class,
                new Box(pos).expand(radius),
                filter
        );

        if (nearby.size() >= maxNearby || world.getRandom().nextInt(chanceOutOf) != 0) return null;

        MobEntity mob = type.create(world);
        if (mob == null) return null;

        mob.refreshPositionAndAngles(
                pos.getX() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 4,
                pos.getY() + 1,
                pos.getZ() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 4,
                world.getRandom().nextFloat() * 360,
                0
        );

        GoalSelector selector = ((MobEntityGoalAccessor) mob).getGoalSelector();
        selector.add(0, new StayNearBlockGoal(mob, pos, 12.0,  0.25));

        world.spawnEntity(mob);
        return mob;
    }

    private static class StayNearBlockGoal extends net.minecraft.entity.ai.goal.Goal {
        private final MobEntity mob;
        private final BlockPos anchor;
        private final double maxDistance;
        private final double speed;

        public StayNearBlockGoal(MobEntity mob, BlockPos anchor, double maxDistance, double speed) {
            this.mob = mob;
            this.anchor = anchor;
            this.maxDistance = maxDistance;
            this.speed = speed;
        }

        @Override
        public boolean canStart() {
            return mob.squaredDistanceTo(Vec3d.ofCenter(anchor)) > maxDistance * maxDistance;
        }

        @Override
        public void start() {
            mob.getNavigation().startMovingTo(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5, speed);
        }

        @Override
        public boolean shouldContinue() {
            return canStart();
        }
    }

    private static int countNearbyMarketCrates(World world, BlockPos pos) {
        Box area = new Box(pos).expand(spawnRadius);
        return (int) BlockPos.stream(area)
                .filter(p -> world.getBlockEntity(p) instanceof MarketCrateBlockEntity)
                .count();
    }

    private static void tryDespawnVillager(World world, ArrayList<MobEntity> villagers, int chanceOutOf) {
        if (world.getRandom().nextInt(chanceOutOf) == 0) {
            if (!villagers.isEmpty()) {
                villagers.removeFirst().discard();
            }
        }
    }
}