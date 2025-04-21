package com.avandortools.simplemarket.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static void initialize() {
    }

    public static final ScreenHandlerType<MarketCrateScreenHandler> MARKET_CRATE =
            new ExtendedScreenHandlerType<>((syncId, inv, buf) ->
                    new MarketCrateScreenHandler(syncId, inv, (PacketByteBuf) buf)
            );

    public static void registerScreenHandlers() {
        // Register your ScreenHandlerType
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of("simplemarket", "market_crate"), MARKET_CRATE);
    }
}
