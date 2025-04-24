package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import com.avandortools.simplemarket.util.AvandorTimeUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.avandortools.simplemarket.SimpleMarket.LOGGER;

public class MarketCatEntity extends CatEntity {

    private BlockPos ownerMarketBlock;

    public MarketCatEntity(EntityType<? extends CatEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient) return;

        nighttimeDespawn();
        noOwnerBlockDespawn();
    }

    private void noOwnerBlockDespawn() {
        if (ownerMarketBlock != null) {
            BlockEntity blockEntity = this.getWorld().getBlockEntity(ownerMarketBlock);
            if (!(blockEntity instanceof MarketCrateBlockEntity)) {
                discard();
            }
        }
    }

    public void setOwnerMarketBlock(BlockPos blockPos) {
        this.ownerMarketBlock = blockPos;
        goalSelector.clear(goal -> true);
        initGoals();
    }

    @Override
    public void initGoals() {
//        super.initGoals();
        if (ownerMarketBlock == null){
            LOGGER.info("skipping adding goal,,,");
            return;
        }
        this.goalSelector.add(0, new AmbientMobSpawner.StayNearBlockGoal(this, ownerMarketBlock, 4, 0.25));
        LOGGER.info("goal initialized");
    }

    private void nighttimeDespawn(){
        if (AvandorTimeUtils.worldIsNight(this.getWorld())) {
            if (this.getWorld().getRandom().nextInt(100) == 0) discard(); // 1% chance to despawn per tick at night
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (ownerMarketBlock != null) {
            nbt.putInt("MarketX", ownerMarketBlock.getX());
            nbt.putInt("MarketY", ownerMarketBlock.getY());
            nbt.putInt("MarketZ", ownerMarketBlock.getZ());
        }
        LOGGER.info("wrote NBT: {}", nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("MarketX") && nbt.contains("MarketY") && nbt.contains("MarketZ")) {
            setOwnerMarketBlock(new BlockPos(nbt.getInt("MarketX"), nbt.getInt("MarketY"), nbt.getInt("MarketZ")));
        }
        LOGGER.info("read NBT: {}", nbt);
    }
}