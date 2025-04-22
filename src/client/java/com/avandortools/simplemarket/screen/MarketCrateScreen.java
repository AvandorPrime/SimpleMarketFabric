package com.avandortools.simplemarket.screen;

import com.avandortools.simplemarket.SimpleMarket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MarketCrateScreen extends HandledScreen<MarketCrateScreenHandler> {

    // A path to the gui texture. In this example we use the texture from the dispenser
    //private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/dispenser.png");
    //private static final Identifier PROGRESS_ARROW_TEXTURE = Identifier.ofVanilla("container/furnace/burn_progress");
    private static final Identifier PROGRESS_ARROW_TEXTURE = Identifier.of(SimpleMarket.MOD_ID, "burn_progress");
    private static final Identifier TEXTURE = Identifier.of(SimpleMarket.MOD_ID, "textures/gui/market_crate.png");

    public MarketCrateScreen(MarketCrateScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
////        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draw the arrow progress bar (like furnace)
        int progress = handler.getProgress();
        int maxProgress = handler.getMaxProgress();
        int arrowWidth = 24;
        int progressWidth = (progress * arrowWidth) / maxProgress;

        context.drawGuiTexture(PROGRESS_ARROW_TEXTURE, 24, 16, 0, 0, x + 79, y + 34, progressWidth, 16);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}