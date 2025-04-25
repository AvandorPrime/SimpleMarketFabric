package com.avandortools.simplemarket.entity.goal;

import com.avandortools.simplemarket.block.MarketCrateBlock;
import com.avandortools.simplemarket.entity.MarketVillagerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class BrowseMarketGoal extends Goal {
    private final MarketVillagerEntity mob;
    private final double speed;
    private final int cooldownLimit;
    private final int radius;
    private BlockPos targetPos;
    private final BlockPos anchorPos;
    private int cooldownTicks;
    private int eatingTicks = 0;

    public BrowseMarketGoal(MarketVillagerEntity mob, double speed, int radius, BlockPos anchorPos, int cooldownTicks) {
        this.mob = mob;
        this.speed = speed;
        this.radius = radius;
        this.cooldownLimit = cooldownTicks;
        this.cooldownTicks = cooldownTicks;
        this.anchorPos = anchorPos;
    }

    @Override
    public EnumSet<Control> getControls() {
        return EnumSet.of(Control.MOVE);
    }

    @Override
    public boolean canStart() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }

        if (mob.getNavigation().isFollowingPath() || mob.getRandom().nextInt(100) < 90)
            return false; // Small random chance to start browsing

        targetPos = findNearbyCrate();
        return targetPos != null;
    }

    @Override
    public void start() {
        System.out.println("BrowseMarketGoal: start");
        if (targetPos != null) {
            mob.getNavigation().startMovingTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
        }
    }

    @Override
    public boolean shouldContinue() {
        return !mob.getNavigation().isIdle() || eatingTicks > 0;
    }

    @Override
    public void stop() {
        System.out.println("BrowseMarketGoal: stop");
        cooldownTicks = cooldownLimit;
        mob.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (mob.squaredDistanceTo(Vec3d.ofCenter(targetPos)) < 2.0) { //we are near a crate
            if (mob.age % 1 == 0) { // once per 5 second
                mob.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0F, 1.0F);
            }
            mob.getLookControl().lookAt(Vec3d.ofCenter(targetPos));
            tickEating();
        }
    }

    //TODO since goal only triggers intermittently the eating dont work to great
    // move eating into a new goal where if eating ticks is set they stand still and eat (then spin)
    private void tickEating(){
        System.out.println("Eating ticks : " + eatingTicks);
        if (eatingTicks > 0){
                mob.setHeadRollingTimeLeft(40);
                mob.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                ((ServerWorld) mob.getWorld()).spawnParticles(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.BREAD)), //TODO: use a crate item
                        mob.getX(), mob.getY() + 1.0, mob.getZ(),
                        5, 0.2, 0.2, 0.2, 0.05
                );
            eatingTicks--;
        } else if (mob.getRandom().nextFloat() < 0.25f || true) {
            eatingTicks = 1000 + mob.getRandom().nextInt(10);
            mob.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BREAD));
        }
    }

    private BlockPos findNearbyCrate() {
        BlockPos.Mutable mutable = anchorPos.mutableCopy();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    mutable.set(anchorPos.getX() + dx, anchorPos.getY() + dy, anchorPos.getZ() + dz);
                    if (mob.getWorld().getBlockState(mutable).getBlock() instanceof MarketCrateBlock) {
                        return mutable.toImmutable();
                    }
                }
            }
        }
        return null;
    }
}
