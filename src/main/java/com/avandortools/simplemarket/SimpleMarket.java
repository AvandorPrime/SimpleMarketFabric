package com.avandortools.simplemarket;

import com.avandortools.simplemarket.block.ModBlocks;
import com.avandortools.simplemarket.block.entity.ModBlockEntities;
import com.avandortools.simplemarket.entity.MarketCatEntity;
import com.avandortools.simplemarket.entity.MarketWolfEntity;
import com.avandortools.simplemarket.entity.MarketVillagerEntity;
import com.avandortools.simplemarket.entity.ModEntityTypes;
import com.avandortools.simplemarket.item.ModItems;
import com.avandortools.simplemarket.screen.ModScreenHandlers;
import com.avandortools.simplemarket.util.SimpleMarketConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMarket implements ModInitializer {
	public static final String MOD_ID = "simplemarket";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SimpleMarketConfig.copyConfigIfNeeded();

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