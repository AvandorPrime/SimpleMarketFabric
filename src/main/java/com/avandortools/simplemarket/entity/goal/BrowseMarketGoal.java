package com.avandortools.simplemarket.entity.goal;

import com.avandortools.simplemarket.block.MarketCrateBlock;
import com.avandortools.simplemarket.entity.MarketVillagerEntity;
import com.avandortools.simplemarket.util.AvandorTimeUtils;
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
    private int spinTicks = 0;
    private int decidingTicks = 0;
    private BrowseState state = BrowseState.WALKING_TO_CRATE;

    public BrowseMarketGoal(MarketVillagerEntity mob, double speed, int radius, BlockPos anchorPos, int cooldownTicks) {
        this.mob = mob;
        this.speed = speed;
        this.radius = radius;
        this.cooldownLimit = cooldownTicks;
        this.cooldownTicks = cooldownTicks;
        this.anchorPos = anchorPos;
    }

    private enum BrowseState { WALKING_TO_CRATE, DECIDING, EATING, SPINNING }

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
            state = BrowseState.WALKING_TO_CRATE;
        }
    }

    @Override
    public boolean shouldContinue() {
        boolean hasStateAndIsNotWalkingToCrate = state != null && state != BrowseState.WALKING_TO_CRATE;
        boolean isWalkingToCrateStateAndMobIsNavigating = state == BrowseState.WALKING_TO_CRATE && !mob.getNavigation().isIdle();
        return hasStateAndIsNotWalkingToCrate || isWalkingToCrateStateAndMobIsNavigating;
    }

    @Override
    public void stop() {
        System.out.println("BrowseMarketGoal: stop");
        cooldownTicks = cooldownLimit;
        mob.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        state = null;
    }

    @Override
    public void tick() {
        switch (state) {
            case WALKING_TO_CRATE:
            if (mob.squaredDistanceTo(Vec3d.ofCenter(targetPos)) < 2.0) {
                mob.getNavigation().stop();
                mob.getLookControl().lookAt(Vec3d.ofCenter(targetPos));
                state = BrowseState.DECIDING;
                decidingTicks = 20 + mob.getRandom().nextInt(10);
                mob.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0F, 1.0F);
            }
            break;
            case DECIDING:
                if (--decidingTicks <= 0) {
                    state = BrowseState.EATING;
                    eatingTicks = 30 + mob.getRandom().nextInt(10);
                    mob.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BREAD));
                }
                break;
            case EATING:
                mob.setHeadRollingTimeLeft(40);
                mob.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F); //playing the sound and spawning particles happen every tick. do we want to reduce this freqency to a once off or every n ticks?
                ((ServerWorld) mob.getWorld()).spawnParticles(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.BREAD)),
                        mob.getX(), mob.getY() + 1.0, mob.getZ(),
                        5, 0.2, 0.2, 0.2, 0.05
                );
                if (--eatingTicks <= 0) {
                    mob.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    state = BrowseState.SPINNING;
                    spinTicks = 48;
                }
                break;
            case SPINNING:
                mob.setYaw(mob.getYaw() + 15.0f);
                mob.setHeadYaw(mob.getYaw());
                if (mob.isOnGround()) {
                    mob.setVelocity(0, 0.4, 0);
                    mob.velocityModified = true;
                }
                ((ServerWorld) mob.getWorld()).spawnParticles(
                        ParticleTypes.HEART,
                        mob.getX(), mob.getY() + 1.0, mob.getZ(),
                        2, 0.2, 0.3, 0.2, 0.01
                );
                if (--spinTicks <= 0) {
                    stop();
                }
                break;
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
