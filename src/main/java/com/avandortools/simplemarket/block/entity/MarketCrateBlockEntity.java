package com.avandortools.simplemarket.block.entity;

import com.avandortools.simplemarket.block.MarketCrateBlock;
import com.avandortools.simplemarket.entity.AmbientMobSpawner;
import com.avandortools.simplemarket.screen.MarketCrateScreenHandler;
import com.avandortools.simplemarket.util.AvandorTimeUtils.TimeConstants;
import com.avandortools.simplemarket.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import com.avandortools.simplemarket.util.SimpleMarketConfig;

import java.util.Collections;

public class MarketCrateBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private int currentProcessingProgress = 0;
    private int currentAmbientSpawnProgress = 0;
//    private static final int MAX_AMBIENT_SPAWN_PROGRESS_BASE = TimeConstants.TICKS_PER_IRL_MINUTE*3;

//    private static final int MAX_AMBIENT_SPAWN_PROGRESS_VARIANCE = TimeConstants.TICKS_PER_IRL_SECOND*30;
    private int currentAmbientSpawnMax = MAX_AMBIENT_SPAWN_PROGRESS_BASE; //hopefully randomized on block placement
//    private static final int MAX_PROCESSING_PROGRESS = TimeConstants.TICKS_PER_IRL_MINUTE*5; // How many ticks to fully process
    private boolean isProcessing = false;
    private static final int MAX_PROCESSING_PROGRESS = SimpleMarketConfig.getInstance().getInt("processing_time_ticks");

    // Debug fast processing
    //    private static final int MAX_PROGRESS = TimeConstants.TICKS_PER_IRL_SECOND*5; // Debug fast processing
    private static final int MAX_AMBIENT_SPAWN_PROGRESS_BASE = TimeConstants.TICKS_PER_IRL_SECOND*3;
    private static final int MAX_AMBIENT_SPAWN_PROGRESS_VARIANCE = TimeConstants.TICKS_PER_IRL_SECOND;

    public MarketCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MARKET_CRATE_BLOCK_ENTITY, pos, state);
    }

    // From the ImplementedInventory Interface
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    // These Methods are from the NamedScreenHandlerFactory Interface
    // createMenu creates the ScreenHandler itself
    // `getDisplayName` will Provide its name which is normally shown at the top
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // We provide *this* to the screenHandler as our class Implements Inventory
        // Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new MarketCrateScreenHandler(syncId, playerInventory, this, this.getPropertyDelegate());
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    // For the following two methods, for earlier versions, remove the parameter `registryLookup`.
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        // Clear the current inventory to avoid leftover client data
        Collections.fill(inventory, ItemStack.EMPTY);

        Inventories.readNbt(nbt, this.inventory, registryLookup);
        isProcessing = nbt.getBoolean("IsProcessing");
        currentAmbientSpawnProgress = nbt.getInt("AmbientSpawnTimer");
//        System.out.println("Read NBT: " + nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putBoolean("IsProcessing", isProcessing);
        nbt.putInt("AmbientSpawnTimer", currentAmbientSpawnProgress);
//        System.out.println("wrote NBT: " + nbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt, registryLookup);  // Pass an empty registry lookup if needed
        return nbt;
    }

    @Override
    public void markDirty() {
        super.markDirty(); // always call this to keep the world save system happy

        // Notify the client
        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
//        System.out.println("market crate inventory marked as dirty");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);  // Create a packet to send block entity data to the client
    }

    public void tick() {
        if (this.getWorld() == null || this.getWorld().isClient) return;
        doItemProcessingTick();
        doAmbientMobProcessingTick();
    }

    private void doAmbientMobProcessingTick() {
        if(isProcessing) {
            //tick ambient mob spawns
            if (++currentAmbientSpawnProgress >= currentAmbientSpawnMax) {
//                System.out.println("ambientSpawnTimer Reached: " + currentAmbientSpawnProgress + "/" + currentAmbientSpawnMax);
                AmbientMobSpawner.tick(this.world, this.pos);
                currentAmbientSpawnProgress = 0;
                setNewRandomMaxAmbientSpawnProgress();
            }
        }
    }

    private void doItemProcessingTick() {
        ItemStack input = inventory.get(0);
        ItemStack output = inventory.get(1);

        if (!input.isEmpty() && output.isEmpty()) {
            setIsProcessing(true);
            currentProcessingProgress++;
            if (currentProcessingProgress >= MAX_PROCESSING_PROGRESS) {
                // Move item from slot 0 to 1
                inventory.set(1, input.split(1));
                if (input.isEmpty()) {
                    inventory.set(0, ItemStack.EMPTY);
                }
                currentProcessingProgress = 0;
                markDirty();
            }
        } else {
            // Reset if conditions aren't met
            currentProcessingProgress = 0;
            setIsProcessing(false);
        }
    }

    public PropertyDelegate getPropertyDelegate() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return index == 0 ? currentProcessingProgress : MAX_PROCESSING_PROGRESS;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) currentProcessingProgress = value;
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    public boolean getIsProcessing(){
        return isProcessing;
    }

    public void setIsProcessing(boolean newValue) {
        if (isProcessing != newValue) {
            markDirty();
            isProcessing = newValue;

            if (world != null) {
                BlockState state = world.getBlockState(pos);
                BlockState newState = state.with(MarketCrateBlock.PROCESSING, isProcessing);
                world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS); // Notify listeners to update state
            }
        }
    }

    public void onBlockDestroyed() {
//        AmbientMobSpawner.destroyAll(spawnedAmbientMobs);
    }

    public void setNewRandomMaxAmbientSpawnProgress() {
        if (this.getWorld() == null) {
            currentAmbientSpawnMax = MAX_AMBIENT_SPAWN_PROGRESS_BASE;
            return;
        }
        currentAmbientSpawnMax = MAX_AMBIENT_SPAWN_PROGRESS_BASE + this.getWorld().random.nextBetween(-MAX_AMBIENT_SPAWN_PROGRESS_VARIANCE, MAX_AMBIENT_SPAWN_PROGRESS_VARIANCE);
        markDirty();
    }
}
