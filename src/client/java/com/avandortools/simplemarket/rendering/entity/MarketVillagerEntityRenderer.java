package com.avandortools.simplemarket.rendering.entity;

import com.avandortools.simplemarket.SimpleMarket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MarketVillagerEntityRenderer extends VillagerEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(SimpleMarket.MOD_ID, "textures/entity/villager_noise.png");

    public MarketVillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.features.clear();
        this.addFeature(new VillagerHeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(VillagerEntity entity) {
        return TEXTURE;
    }
}
