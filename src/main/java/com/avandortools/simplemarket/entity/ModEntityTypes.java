package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.SimpleMarket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityTypes {

    public static void initialize(

    ) { }
    // Register the custom MarketVillager entity type
    public static final EntityType<MarketVillagerEntity> MARKET_VILLAGER =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(SimpleMarket.MOD_ID, "market_villager"),
                    EntityType.Builder.create(MarketVillagerEntity::new, SpawnGroup.CREATURE)
                            .dimensions(0.6f, 1.95f) // Entity dimensions (example)
                            .build("market_villager")
            );



}