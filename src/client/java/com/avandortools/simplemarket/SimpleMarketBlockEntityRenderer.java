package com.avandortools.simplemarket;

import com.avandortools.simplemarket.block.entity.ModBlockEntities;
import com.avandortools.simplemarket.rendering.blockentity.CounterBlockEntityRenderer;
import com.avandortools.simplemarket.rendering.blockentity.MarketCrateBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class SimpleMarketBlockEntityRenderer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.COUNTER_BLOCK_ENTITY, CounterBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MARKET_CRATE_BLOCK_ENTITY, MarketCrateBlockEntityRenderer::new);
    }
}