package com.avandortools.simplemarket.rendering.entity;

import com.avandortools.simplemarket.entity.MarketCatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MarketCatEntityRenderer extends CatEntityRenderer{
//    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/cat/ocelot.png");

    public MarketCatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

//    public Identifier getTexture(MarketCatEntity ocelotEntity) {
//        return TEXTURE;
//    }
}