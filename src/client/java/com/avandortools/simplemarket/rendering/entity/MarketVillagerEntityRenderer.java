package com.avandortools.simplemarket.rendering.entity;

import com.avandortools.simplemarket.entity.MarketVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;

@Environment(EnvType.CLIENT)
public class MarketVillagerEntityRenderer extends MobEntityRenderer<MarketVillagerEntity, VillagerResemblingModel<MarketVillagerEntity>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/villager/villager.png");

    public MarketVillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER)), 0.5F);
        this.addFeature(new HeadFeatureRenderer(this, context.getModelLoader(), context.getHeldItemRenderer()));
        this.addFeature(new VillagerHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
    }

    public Identifier getTexture(MarketVillagerEntity villagerEntity) {
        return TEXTURE;
    }

    protected void scale(MarketVillagerEntity villagerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F * villagerEntity.getScaleFactor();
        matrixStack.scale(g, g, g);
    }

    protected float getShadowRadius(MarketVillagerEntity villagerEntity) {
        float f = super.getShadowRadius(villagerEntity);
        return villagerEntity.isBaby() ? f * 0.5F : f;
    }
}
