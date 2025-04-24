package com.avandortools.simplemarket.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MarketVillagerEntity extends VillagerEntity {

    private BlockPos ownerMarketBlock;

    public MarketVillagerEntity(EntityType<? extends VillagerEntity> type, World world) {
        super(type, world);
    }

    // Custom behavior (e.g., check for nearby Market Crates)
    @Override
    public void tick() {
        super.tick();
        // Additional custom behavior can be added here (e.g., despawn at night)
    }

    public void setOwnerMarketBlock(BlockPos blockPos) {
        this.ownerMarketBlock = blockPos;
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new AmbientMobSpawner.StayNearBlockGoal(this, ownerMarketBlock, 4, 0.25));
    }

    public static DefaultAttributeContainer.Builder createMarketVillagerAttributes() {
        return VillagerEntity.createVillagerAttributes(); // Or modify as needed
    }
}