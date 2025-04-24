package com.avandortools.simplemarket.entity;

import com.avandortools.simplemarket.SimpleMarket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModEntityTypes {

    public static class MarketVillagerEntity extends MarketMobEntity {
        public MarketVillagerEntity(EntityType<? extends MarketMobEntity> type, World world) {
            super(type, world);
        }
    }
    public static class MarketCatEntity extends MarketMobEntity {
        public MarketCatEntity(EntityType<? extends MarketMobEntity> type, World world) {
            super(type, world);
        }
    }
    public static class MarketWolfEntity extends MarketMobEntity {
        public MarketWolfEntity(EntityType<? extends MarketMobEntity> type, World world) {
            super(type, world);
        }
    }

    public static void initialize() { }

    // Register the custom MarketVillager entity type
    public static final EntityType<MarketVillagerEntity> MARKET_VILLAGER =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(SimpleMarket.MOD_ID, "market_villager"),
                    EntityType.Builder.create(MarketVillagerEntity::new, SpawnGroup.CREATURE)
                            .dimensions(0.6f, 1.95f) // Entity dimensions (example)
                            .build("market_villager")
            );

    public static final EntityType<MarketCatEntity> MARKET_CAT =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(SimpleMarket.MOD_ID, "market_cat"),
                    EntityType.Builder.create(MarketCatEntity::new, SpawnGroup.CREATURE)
                            .dimensions(0.6f, 1.95f) // Entity dimensions (example)
                            .build("market_cat")
            );

    public static final EntityType<MarketWolfEntity> MARKET_WOLF =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(SimpleMarket.MOD_ID, "market_wolf"),
                    EntityType.Builder.create(MarketWolfEntity::new, SpawnGroup.CREATURE)
                            .dimensions(0.6f, 1.95f) // Entity dimensions (example)
                            .build("market_wolf")
            );
}