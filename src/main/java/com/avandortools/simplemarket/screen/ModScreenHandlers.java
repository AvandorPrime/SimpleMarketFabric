package com.avandortools.simplemarket.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static void initialize() {
    }
    public static final ScreenHandlerType<MarketCrateScreenHandler> MARKET_CRATE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of("simplemarket", "market_crate"), new ScreenHandlerType<>(MarketCrateScreenHandler::new, FeatureSet.empty()));
}
