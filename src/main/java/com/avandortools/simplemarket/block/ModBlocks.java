package com.avandortools.simplemarket.block;

import com.avandortools.simplemarket.SimpleMarket;
import com.avandortools.simplemarket.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        // Register the block and its item.
        Identifier id = Identifier.of(SimpleMarket.MOD_ID, name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {
        // Register the group.
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);

// Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemGroup -> {
            itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE);
            itemGroup.add(ModBlocks.CONDENSED_DIRT.asItem());
            itemGroup.add(ModBlocks.CONDENSED_OAK_LOG.asItem());
            itemGroup.add(ModBlocks.PRISMARINE_LAMP.asItem());
            itemGroup.add(ModBlocks.COUNTER_BLOCK.asItem());
            itemGroup.add(ModBlocks.MARKET_CRATE.asItem());
            // ...
        });
    }

    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(SimpleMarket.MOD_ID, "item_group"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.SUSPICIOUS_SUBSTANCE))
            .displayName(Text.translatable("itemGroup.simplemarket"))
            .build();

    public static final Block CONDENSED_DIRT = register(
            new Block(AbstractBlock.Settings.copy(Blocks.DIRT)),
            "condensed_dirt",
            true
    );

    public static final Block CONDENSED_OAK_LOG = register(
            new PillarBlock(
                    AbstractBlock.Settings.create()
                            .sounds(BlockSoundGroup.WOOD)
            ), "condensed_oak_log", true
    );

    public static final Block PRISMARINE_LAMP = register(
            new PrismarineLampBlock(AbstractBlock.Settings.create()),
            "prismarine_lamp",
            true
    );

    public static final Block COUNTER_BLOCK = register(
            new CounterBlock(AbstractBlock.Settings.create()), "counter_block", true
    );

    public static final Block MARKET_CRATE = register(
            new MarketCrateBlock(AbstractBlock.Settings.create()), "market_crate_block", true
    );
}