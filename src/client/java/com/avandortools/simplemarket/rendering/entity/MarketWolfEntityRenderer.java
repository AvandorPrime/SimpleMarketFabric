package com.avandortools.simplemarket.rendering.entity;

import com.avandortools.simplemarket.SimpleMarket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MarketWolfEntityRenderer extends WolfEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(SimpleMarket.MOD_ID, "textures/entity/wolf.png");

    public MarketWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(WolfEntity wolfEntity) {
        return TEXTURE;
    }
}
