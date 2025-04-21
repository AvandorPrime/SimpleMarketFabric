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
        System.out.println("crate ItemStack: " + entity.getItems());
    }

    private void renderItemOnBlock(ItemStack itemStack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Adjust the position of the item on the block
        matrices.push();
        matrices.translate(0.5, 1.0, 0.5); // This centers the item on top of the block

        // Render the item
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, null, 0);

        matrices.pop();
    }
}