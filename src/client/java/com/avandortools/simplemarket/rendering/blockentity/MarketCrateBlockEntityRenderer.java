package com.avandortools.simplemarket.rendering.blockentity;

import com.avandortools.simplemarket.block.MarketCrateBlock;
import com.avandortools.simplemarket.block.entity.MarketCrateBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import  org.joml.Vector3f;

public class MarketCrateBlockEntityRenderer implements BlockEntityRenderer<MarketCrateBlockEntity> {
    private final ItemRenderer itemRenderer;

    public MarketCrateBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MarketCrateBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack itemStack = entity.getStack(0);
        if (!itemStack.isEmpty()) {
            BlockState state = entity.getCachedState();
            Direction facing = state.get(MarketCrateBlock.HORIZONTAL_FACING);
            renderItemOnBlock(itemStack, matrices, vertexConsumers, light, overlay, facing);
        }
//        System.out.println("crate ItemStack: " + entity.getItems());
        if (entity.getIsProcessing() && Math.random() < 0.05) {
            spawnGoldParticles(entity.getWorld(), entity.getPos());
        }
    }

    private void renderItemOnBlock(ItemStack itemStack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing) {
        double[][] positions = getPositions();

        // Start by centering the matrix stack to the middle of the block
        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        // Rotate around Y axis based on block facing
        float rotation = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f;
        };
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        // Move back to origin so translations apply relative to block corner
        matrices.translate(-0.5, 0, -0.5);

        itemStack.getCount();
        for (int i = 0; i < Math.min(positions.length, itemStack.getCount()); i++) {
            double[] pos = positions[i];
            matrices.push();

            matrices.translate(pos[0], 2.0/16.0, pos[1]);
            matrices.scale(0.8f, 0.8f, 0.8f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45));

            itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, null, 0);

            matrices.pop();
        }
        matrices.pop();
    }

    private static double[] @NotNull [] getPositions() {
        double padding = 2/16.0;
        double quarteredRemainingSpace = (12.0/16.0)/4;
        double itemSize = 10.0/16.0;

        double offsetInnerX =  padding + quarteredRemainingSpace;
        double offsetInnerZ = offsetInnerX - itemSize/8;
        double offsetOuterX = 1 - offsetInnerX;
        double offsetOuterZ = offsetOuterX - itemSize/8;

        return new double[][]{
                {offsetInnerX, offsetInnerZ},
                {offsetInnerX, (offsetInnerZ+offsetOuterZ)/2},
                {offsetInnerX, offsetOuterZ},
                {offsetOuterX, offsetInnerZ},
                {offsetOuterX, (offsetInnerZ+offsetOuterZ)/2},
                {offsetOuterX, offsetOuterZ}
        };
    }

    private long lastParticleTick = 0;
    public void spawnGoldParticles(World world, BlockPos pos) {
        long gameTime = world.getTime();

        // Only spawn every 10 ticks or so
        if (gameTime - lastParticleTick < 10) return;
        lastParticleTick = gameTime;

        if (world.isClient()) {
            // Adjust these values for how you want the particles to spawn
            for (int i = 0; i < 1; i++) {
                double x = pos.getX() + 0.5 + (Math.random() - 0.5) * 0.6;
                double y = pos.getY() + 0.3 + Math.random() * 0.3;
                double z = pos.getZ() + 0.5 + (Math.random() - 0.5) * 0.6;

                // Spawn a gold particle
                world.addParticle(
                        new DustParticleEffect(new Vector3f(1.0f, 0.85f, 0.2f), 0.5f), // RGB + scale
                        x, y, z,
                        0.0D, 0.1D, 0.0D
                );
            }
        }
    }
}