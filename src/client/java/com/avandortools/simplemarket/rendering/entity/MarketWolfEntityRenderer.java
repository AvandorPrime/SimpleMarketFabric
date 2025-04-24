package com.avandortools.simplemarket.rendering.entity;

import com.avandortools.simplemarket.entity.MarketWolfEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MarketWolfEntityRenderer extends MobEntityRenderer<MarketWolfEntity, WolfEntityModel<MarketWolfEntity>> {
        public MarketWolfEntityRenderer(EntityRendererFactory.Context context) {
            super(context, new WolfEntityModel<>(context.getPart(EntityModelLayers.WOLF)), 0.5F);
        }

        public void render(MarketWolfEntity wolfEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
            super.render(wolfEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }

        public Identifier getTexture(MarketWolfEntity wolfEntity) {
            return Identifier.ofVanilla("textures/entity/wolf/wolf.png");
        }
    }
