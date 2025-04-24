package com.avandortools.simplemarket;

import com.avandortools.simplemarket.block.ModBlocks;
import com.avandortools.simplemarket.block.entity.ModBlockEntities;
import com.avandortools.simplemarket.entity.MarketCatEntity;
import com.avandortools.simplemarket.entity.MarketWolfEntity;
import com.avandortools.simplemarket.entity.ModEntityTypes.MarketVillagerEntity;
import com.avandortools.simplemarket.entity.ModEntityTypes;
import com.avandortools.simplemarket.item.ModItems;
import com.avandortools.simplemarket.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMarket implements ModInitializer {
	public static final String MOD_ID = "simplemarket";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModBlocks.initialize();
		ModItems.initialize();
		ModBlockEntities.initialize();
		ModScreenHandlers.initialize();
		ModEntityTypes.initialize();
		LOGGER.info("Hello From SimpleMarket!");

		FabricDefaultAttributeRegistry.register(ModEntityTypes.MARKET_VILLAGER,
				MarketVillagerEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.MARKET_CAT,
				MarketCatEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ModEntityTypes.MARKET_WOLF,
				MarketWolfEntity.createMobAttributes());
	}
}