package com.avandortools.simplemarket.rendering.blockentity;

import com.avandortools.simplemarket.block.entity.CounterBlockEntity;
import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class MarketCrateBlockEntityRenderer implements BlockEntityRenderer<MarketCrateBlockEntity> {
    private final ItemRenderer itemRenderer;

    public MarketCrateBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MarketCrateBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.isEmpty()) {
            ItemStack itemStack = entity.getFirstNonempty();
            renderItemOnBlock(itemStack, matrices, vertexConsumers, light, overlay);
        }
//        System.out.println("crate ItemStack: " + entity.getItems());
    }

    private void renderItemOnBlock(ItemStack itemStack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        double offsetMargin = 2/16.0 + (12.0/16.0)/4;
        double itemSize = 10.0/16.0; //half because we scale 0.5?
        double offsetInnerX = offsetMargin;
        double offsetInnerZ = offsetMargin - itemSize/8;
        double offsetOuterX = 1 - offsetMargin;
        double offsetOuterZ = offsetOuterX - itemSize/8;

        double[][] positions = {
                {offsetInnerX, offsetInnerZ},
                {offsetInnerX, offsetOuterZ},
                {offsetOuterX, offsetInnerZ},
                {offsetOuterX, offsetOuterZ}
        };
        for (double[] pos : positions) {
            matrices.push();


            matrices.translate(pos[0], 2.0/16.0, pos[1]); //TODO: change 1 back to 1/16 when fin debugging
            matrices.scale(0.5f, 0.1f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

            itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, null, 0);

            matrices.pop();
        }
    }
}