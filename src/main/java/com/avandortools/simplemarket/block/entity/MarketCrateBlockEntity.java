package com.avandortools.simplemarket.block.entity;

import com.avandortools.simplemarket.block.MarketCrateBlock;
import com.avandortools.simplemarket.entity.AmbientMobSpawner;
import com.avandortools.simplemarket.screen.MarketCrateScreenHandler;
import com.avandortools.simplemarket.util.AvandorTimeUtils.TimeConstants;
import com.avandortools.simplemarket.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.MobEntity;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketCrateBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
//    private final List<UUID> villagerUUIDs = new ArrayList<>();
    private final ArrayList<MobEntity> villagers = new ArrayList<>();
    private int ambientSpawnTimer = 200;
    //private int ambientSpawnStartValue = TimeConstants.TICKS_PER_IRL_MINUTE*5 + world.random.nextInt(TimeConstants.TICKS_PER_IRL_MINUTE*10);
    private final int ambientSpawnStartValue = 200;

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
        for (int i = 0; i < inventory.size(); ++i) {
            inventory.set(i, ItemStack.EMPTY);
        }

        Inventories.readNbt(nbt, this.inventory, registryLookup);
        isProcessing = nbt.getBoolean("IsProcessing");

        ambientSpawnTimer = nbt.getInt("AmbientSpawnTimer");
        this.villagers.clear();
        NbtList list = nbt.getList("villagerUUIDs", NbtElement.STRING_TYPE);

        for (NbtElement element : list) {
            if (element instanceof NbtString) {
                String uuidString = ((NbtString) element).asString();
                this.villagers.add((MobEntity) ((ServerWorld) world).getEntity(UUID.fromString(uuidString)));
            }
        }
        nbt.put("villagerUUIDs", list);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putBoolean("IsProcessing", isProcessing);

        nbt.putInt("AmbientSpawnTimer", ambientSpawnTimer);
        NbtList list = new NbtList();
        for (MobEntity villager : villagers) {
            list.add(NbtHelper.fromUuid(villager.getUuid()));
        }
        nbt.put("villagerUUIDs", list);
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
        System.out.println("market crate inventory marked as dirty");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);  // Create a packet to send block entity data to the client
    }

    //this is where we sell items for coins $$$$ #capitalism #money #cringemod
    public void processItems() {
        ItemStack inputItem = inventory.get(0); // Slot 0 (input)
        if (!inputItem.isEmpty()) {
            // Move item from Slot 0 to Slot 1 (output)
            inventory.set(1, inputItem.copy());
            inventory.set(0, ItemStack.EMPTY); // Empty Slot 0
            markDirty(); // Mark for saving
        }
    }

    private int progress = 0;
//    private static final int MAX_PROGRESS = 5000; // How many ticks to fully process
    private static final int MAX_PROGRESS = 100; // How many ticks to fully process
    private boolean isProcessing = false;
    public void tick() {
        if (!this.world.isClient) {
            ItemStack input = inventory.get(0);
            ItemStack output = inventory.get(1);

            if (!input.isEmpty() && output.isEmpty()) {
                setIsProcessing(true);
                progress++;
                if (progress >= MAX_PROGRESS) {
                    // Move item from slot 0 to 1
                    inventory.set(1, input.split(1));
                    if (input.isEmpty()) {
                        inventory.set(0, ItemStack.EMPTY);
                    }
                    progress = 0;
                    markDirty();
                }
            } else {
                // Reset if conditions aren't met
                progress = 0;
                setIsProcessing(false);
            }
            AmbientMobSpawner.tick(this.world, this.pos, villagers, --ambientSpawnTimer);
            System.out.println("ambientSpawnTimer: " + ambientSpawnTimer);
            if (ambientSpawnTimer <= 0) ambientSpawnTimer = 200;
        }
    }

    public PropertyDelegate getPropertyDelegate() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return index == 0 ? progress : MAX_PROGRESS;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) progress = value;
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
        AmbientMobSpawner.destroyAllVillagers(villagers);
        villagers.clear();
    }
}
