package com.avandortools.simplemarket;

import com.avandortools.simplemarket.entity.ModEntityTypes;
import com.avandortools.simplemarket.screen.MarketCrateScreen;
import com.avandortools.simplemarket.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.VillagerEntityRenderer;

public class SimpleMarketClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		HandledScreens.register(ModScreenHandlers.MARKET_CRATE_SCREEN_HANDLER, MarketCrateScreen::new);

		EntityRendererRegistry.register(ModEntityTypes.MARKET_VILLAGER, VillagerEntityRenderer::new);
	}
}