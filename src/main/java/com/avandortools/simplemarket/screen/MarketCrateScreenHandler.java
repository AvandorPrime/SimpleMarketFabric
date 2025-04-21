package com.avandortools.simplemarket.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class MarketCrateScreenHandler extends ScreenHandler {
    private final BlockEntity blockEntity;

    // CLIENT-SIDE constructor
    public MarketCrateScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public MarketCrateScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.MARKET_CRATE, syncId);
        this.blockEntity = blockEntity;

        // Add 1 slot for the block entity (crate)
        this.addSlot(new Slot((Inventory) blockEntity, 0, 80, 35));

        // Player Inventory (3 rows of 9 slots)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (1 row of 9 slots)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public MarketCrateScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.MARKET_CRATE, syncId);
        blockEntity = null;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack originalStack = slot.getStack();
        ItemStack newStack = originalStack.copy();

        int blockInventorySize = 1; // since we only have 1 slot for the crate
        int playerInventoryStart = blockInventorySize;
        int playerInventoryEnd = playerInventoryStart + 27; // main inventory
        int hotbarEnd = playerInventoryEnd + 9;

        if (index < blockInventorySize) {
            // From block to player
            if (!this.insertItem(originalStack, playerInventoryStart, hotbarEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // From player to block
            if (!this.insertItem(originalStack, 0, blockInventorySize, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}


