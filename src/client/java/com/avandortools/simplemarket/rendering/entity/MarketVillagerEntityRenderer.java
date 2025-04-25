package com.avandortools.simplemarket.rendering.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;

@Environment(EnvType.CLIENT)
public class MarketVillagerEntityRenderer extends VillagerEntityRenderer {
//    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/villager/villager.png");

    public MarketVillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}
