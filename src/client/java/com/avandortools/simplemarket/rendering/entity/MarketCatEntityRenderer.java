package com.avandortools.simplemarket.rendering.entity;

import com.avandortools.simplemarket.SimpleMarket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MarketCatEntityRenderer extends CatEntityRenderer{
    private static final Identifier TEXTURE = Identifier.of(SimpleMarket.MOD_ID,"textures/entity/cat.png");

    public MarketCatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(CatEntity meow) {
        return TEXTURE;
    }
}