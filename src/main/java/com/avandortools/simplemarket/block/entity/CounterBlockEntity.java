package com.avandortools.simplemarket.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CounterBlockEntity extends BlockEntity {
    private int clicks = 0;
    private int ticksSinceLast = 0;

    public CounterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COUNTER_BLOCK_ENTITY, pos, state);
    }

    public int getClicks() {
        return clicks;
    }
    public void incrementClicks() {
        if (ticksSinceLast < 10) return;
        ticksSinceLast = 0;
        clicks++;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("clicks", clicks);
        nbt.putInt("ticksSinceLast", ticksSinceLast);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        clicks = nbt.getInt("clicks");
        ticksSinceLast = nbt.getInt("ticksSinceLast");
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, CounterBlockEntity entity) {
        entity.ticksSinceLast++;
    }

}