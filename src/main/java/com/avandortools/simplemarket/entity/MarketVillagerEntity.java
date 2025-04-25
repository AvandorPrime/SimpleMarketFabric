package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import com.avandortools.simplemarket.entity.goal.BrowseMarketGoal;
import com.avandortools.simplemarket.entity.goal.StayNearMarketGoal;
import com.avandortools.simplemarket.entity.goal.WanderWithinRadiusGoal;
import com.avandortools.simplemarket.util.AvandorTimeUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import static com.avandortools.simplemarket.SimpleMarket.LOGGER;

public class MarketVillagerEntity extends VillagerEntity {

    private static final int MAX_DIST_FROM_MARKET = 16;
    private BlockPos ownerMarketBlock;

    public MarketVillagerEntity(EntityType<? extends VillagerEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        //consumeAvailableFood

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
        if (ownerMarketBlock == null){
            return;
        }
        this.goalSelector.add(0, new StayNearMarketGoal(this, ownerMarketBlock, MAX_DIST_FROM_MARKET, 0.25));
        this.goalSelector.add(1, new BrowseMarketGoal(this, 0.8, MAX_DIST_FROM_MARKET, ownerMarketBlock, 1));
        this.goalSelector.add(2, new WanderWithinRadiusGoal(this, ownerMarketBlock, 0.25, MAX_DIST_FROM_MARKET));
        LOGGER.info("goals initialized");
    }

    private void nighttimeDespawn(){
        if (AvandorTimeUtils.worldIsNight(this.getWorld())) {
            if (this.getWorld().getRandom().nextInt(100) == 0) discard(); // 1% chance to despawn per tick at night
        }
    }

    //hopefully we wont need this as our goals won't cause us to wander off to colonize africa
    private void tickHardLeashToBlock(){
        if (ownerMarketBlock != null && !this.getBlockPos().isWithinDistance(ownerMarketBlock, MAX_DIST_FROM_MARKET)) {
            this.getNavigation().startMovingTo(ownerMarketBlock.getX(), ownerMarketBlock.getY(), ownerMarketBlock.getZ(), 0.25);
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

    /**Lobotomize **/
    @Override
    protected Brain.Profile<VillagerEntity> createBrainProfile() {
        // Return an empty profile so no tasks are loaded
        return Brain.createProfile(ImmutableList.of(), ImmutableList.of());
    }
}
