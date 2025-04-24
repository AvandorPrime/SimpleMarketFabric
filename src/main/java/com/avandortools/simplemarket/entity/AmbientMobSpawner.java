package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import com.avandortools.simplemarket.util.AvandorTimeUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

import static com.avandortools.simplemarket.SimpleMarket.LOGGER;

/**
 * Responsible for spawning and despawning market entities based on the time of day and current nearbyt entities
 */
public class AmbientMobSpawner {
    static final int spawnRadius = 32;
    public static void tick(World world, BlockPos pos){
        if (!AvandorTimeUtils.worldIsNight(world)) {
            final int crateCount = countNearbyMarketCrates(world, pos);
            trySpawnAmbientMob(world, pos, ModEntityTypes.MARKET_CAT, 2, 1);
            trySpawnAmbientMob(world, pos, ModEntityTypes.MARKET_WOLF, 2, 1);
            trySpawnAmbientMob(world, pos, ModEntityTypes.MARKET_VILLAGER, (int) Math.ceil(0.5*crateCount), 1);
        }
    }

    private static void trySpawnAmbientMob(World world, BlockPos pos, EntityType<? extends MobEntity> type, int maxNearby, int chanceOutOf) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, false) == null) return;

        Predicate<MobEntity> filter = mob -> mob.getType() == type && !mob.isPersistent();

        List<MobEntity> nearby = serverWorld.getEntitiesByClass(
                MobEntity.class,
                new Box(pos).expand(spawnRadius),
                filter
        );

        if (nearby.size() >= maxNearby || world.getRandom().nextInt(chanceOutOf) != 0) return;

        MobEntity mob = type.create(world);
        if (mob == null) return;

        mob.refreshPositionAndAngles(
                pos.getX() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 4,
                pos.getY() + 1,
                pos.getZ() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 4,
                world.getRandom().nextFloat() * 360,
                0
        );

        if (type == ModEntityTypes.MARKET_VILLAGER){
            ((MarketVillagerEntity) mob).setOwnerMarketBlock(pos);
        } else if (type == ModEntityTypes.MARKET_CAT) {
            ((MarketCatEntity) mob).setOwnerMarketBlock(pos);
        } else if (type == ModEntityTypes.MARKET_WOLF) {
            ((MarketWolfEntity) mob).setOwnerMarketBlock(pos);
        }
        world.spawnEntity(mob);
    }

    public static class StayNearBlockGoal extends net.minecraft.entity.ai.goal.Goal {
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
            if (anchor == null) return false;
            return mob.squaredDistanceTo(Vec3d.ofCenter(anchor)) > maxDistance * maxDistance;
        }

        @Override
        public void start() {
            LOGGER.info("job start :{}", anchor);
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
}