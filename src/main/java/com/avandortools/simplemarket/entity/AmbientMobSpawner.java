package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import com.avandortools.simplemarket.mixin.MobEntityGoalAccessor;
import com.avandortools.simplemarket.util.AvandorTimeUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Responsible for spawning and despawning market entities based on the time of day and current nearbyt entities
 */
public class AmbientMobSpawner {
    static final int spawnRadius = 32;
    public static void tick(World world, BlockPos pos, AmbientMobGroup ambientMobGroup){
        if (AvandorTimeUtils.worldIsNight(world)) {
            tryDespawnVillager(world, ambientMobGroup, 10);
        } else {
            final int crateCount = countNearbyMarketCrates(world, pos);
//            trySpawnAmbientMob(world, pos, EntityType.CAT, spawnRadius, 2, 100, ambientMobGroup);
            trySpawnAmbientMob(world, pos, EntityType.CAT, spawnRadius, 2, 1, ambientMobGroup.cats);
            trySpawnAmbientMob(world, pos, EntityType.WOLF, spawnRadius, 2, 1000, ambientMobGroup.wolves);
            trySpawnAmbientMob(world, pos, ModEntityTypes.MARKET_VILLAGER, spawnRadius, (int) Math.ceil(0.5*crateCount), 1, ambientMobGroup.villagers);
        }
    }

    public static void destroyAll(AmbientMobGroup ambientMobGroup) {
        DestroySingleGroup(ambientMobGroup.villagers);
        DestroySingleGroup(ambientMobGroup.cats);
        DestroySingleGroup(ambientMobGroup.wolves);
    }

    private static void DestroySingleGroup(List<MobEntity> entities) {
        Iterator<MobEntity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            MobEntity entity = iterator.next();
            if (!entity.isRemoved()) {
                entity.discard();
            }
            iterator.remove();
        }
    }

    private static void trySpawnAmbientMob(World world, BlockPos pos, EntityType<? extends MobEntity> type, int radius, int maxNearby, int chanceOutOf, List<MobEntity> spawnedMobs) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, false) == null) return;

        Predicate<MobEntity> filter = mob -> mob.getType() == type && !mob.isPersistent();

        List<MobEntity> nearby = serverWorld.getEntitiesByClass(
                MobEntity.class,
                new Box(pos).expand(radius),
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

//        GoalSelector selector = ((MobEntityGoalAccessor) mob).getGoalSelector();
//        selector.add(0, new StayNearBlockGoal(mob, pos, 4.0,  0.25));

        if (type == ModEntityTypes.MARKET_VILLAGER){
            ((MarketVillagerEntity) mob).setOwnerMarketBlock(pos);
        }
        world.spawnEntity(mob);
        spawnedMobs.add(mob);
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
            mob.getNavigation().startMovingTo(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5, speed);
        }

        @Override
        public boolean shouldContinue() {
            return canStart();
        }

        @Override
        public void tick(){
            mob.bodyYaw += 15;
            if (mob.bodyYaw >= 360) {
                mob.bodyYaw -= 360;
            }
            mob.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER,
                    mob.getX(), mob.getY() + 1.5, mob.getZ(),
                    0.0, 0.1, 0.0);
        }
    }

    private static int countNearbyMarketCrates(World world, BlockPos pos) {
        Box area = new Box(pos).expand(spawnRadius);
        return (int) BlockPos.stream(area)
                .filter(p -> world.getBlockEntity(p) instanceof MarketCrateBlockEntity)
                .count();
    }

    private static void tryDespawnVillager(World world, AmbientMobGroup ambientMobGroup, int chanceOutOf) {
        if (world.getRandom().nextInt(chanceOutOf) == 0) {
            if (!ambientMobGroup.villagers.isEmpty()) {
                ambientMobGroup.villagers.removeFirst().discard();
            }
        }
    }

    public static class AmbientMobGroup {
        public List<MobEntity> villagers = new ArrayList<>();
        public List<MobEntity> cats = new ArrayList<>();
        public List<MobEntity> wolves = new ArrayList<>();

        private final String villagerNBTKey = "villagerUUIDs";
        private final String catNBTKey = "catUUIDs";
        private final String wolfNBTKey = "wolfUUIDs";

        public void clearAll() {
            villagers.clear();
            cats.clear();
            wolves.clear();
        }

        public void writeAllToNBT(NbtCompound nbt, World world){
            if (world.isClient || !(world instanceof ServerWorld serverWorld)) return; //TODO: change to "is my chunk loaded"
            System.out.println("i didnt get skipped");
            writeSingleToNBT(nbt, villagerNBTKey, villagers);
            writeSingleToNBT(nbt, catNBTKey, cats);
            writeSingleToNBT(nbt, wolfNBTKey, wolves);
        }

        private void writeSingleToNBT(NbtCompound nbt, String key, List<MobEntity> entities) {
            NbtList list = new NbtList();
            for (MobEntity entity : entities) {
                list.add(NbtHelper.fromUuid(entity.getUuid()));
            }
            nbt.put(key, list);
        }

//        public void writeAllToNBT(NbtCompound nbt, World world){
//            writeSingleToNBT(nbt, villagerNBTKey, villagers);
//        }
//
//        private void writeSingleToNBT(NbtCompound nbt, String key, List<MobEntity> entities) {
//            NbtList entityList = new NbtList();
//            NbtCompound entityTag = new NbtCompound();
//
//            for (MobEntity entity : entities) {
//                entityTag.putString("uuid", entity.getUuidAsString());
//                entityTag.putInt("chunkX", entity.getBlockPos().getX() >> 4);
//                entityTag.putInt("chunkZ", entity.getBlockPos().getZ() >> 4);
//                entityList.add(entityTag);
//            }
//            nbt.put(key, entityList);
//        }

        public void readAllToNBT(NbtCompound nbt, World world, BlockPos pos){
            clearAll();
            readSingleToNBT(nbt, world, pos, villagerNBTKey, villagers);
            readSingleToNBT(nbt, world, pos, catNBTKey, cats);
            readSingleToNBT(nbt, world, pos, wolfNBTKey, wolves);
        }

        private void readSingleToNBT(NbtCompound nbt, World world, BlockPos pos, String key, List<MobEntity> entities) {
            NbtList list = nbt.getList(key, NbtElement.STRING_TYPE);

            for (NbtElement element : list) {
                if (element instanceof NbtString) {
                    String uuidString = element.asString();
                    MobEntity loadedMob = (MobEntity) ((ServerWorld) world).getEntity(UUID.fromString(uuidString));
                    entities.add(loadedMob);

                    GoalSelector selector = ((MobEntityGoalAccessor) loadedMob).getGoalSelector();
                    selector.add(0, new StayNearBlockGoal(loadedMob, pos, 4.0,  0.25));
                    System.out.println("goal reattached to: " +loadedMob + " bound to: " + pos);
                }
            }
        }
    }
}