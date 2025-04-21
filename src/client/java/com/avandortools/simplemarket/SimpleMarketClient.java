package com.avandortools.simplemarket;

import com.avandortools.simplemarket.rendering.screen.MarketCrateScreen;
import com.avandortools.simplemarket.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SimpleMarketClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
//		ModScreens.registerScreens();
		HandledScreens.register(ModScreenHandlers.MARKET_CRATE, MarketCrateScreen::new);
	}
}