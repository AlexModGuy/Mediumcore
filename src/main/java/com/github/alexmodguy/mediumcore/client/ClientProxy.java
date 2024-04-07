package com.github.alexmodguy.mediumcore.client;

import com.github.alexmodguy.mediumcore.CommonProxy;
import com.github.alexmodguy.mediumcore.Mediumcore;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    private static final ResourceLocation MEDIUMCORE_HEARTS_TEXTURE = new ResourceLocation("mediumcore:textures/gui/mediumcore_hearts.png");
    private static final RandomSource random = RandomSource.create();
    private boolean mediumcoreMode;

    public void clientInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (Mediumcore.CONFIG.mediumcoreHeartTexture.get() && event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) && Minecraft.getInstance().gameMode.canHurtPlayer() && mediumcoreMode && Minecraft.getInstance().getCameraEntity() instanceof Player && Gui.HeartType.forPlayer(player) == Gui.HeartType.NORMAL) {
            int leftHeight = 39;
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float) attrMaxHealth.getValue();
            float absorb = Mth.ceil(player.getAbsorptionAmount());
            int health = Mth.ceil(player.getHealth());
            int forgeGuiTick = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? forgeGui.getGuiTicks() : 0;

            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            random.setSeed((long) (forgeGuiTick * 312871));
            int left = width / 2 - 91;
            int top = height - leftHeight;
            int regen = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) {
                regen = forgeGuiTick % Mth.ceil(healthMax + 5.0F);
            }
            final int heartV = player.level().getLevelData().isHardcore() ? 9 : 0;
            int heartU = 0;
            event.getGuiGraphics().pose().pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, MEDIUMCORE_HEARTS_TEXTURE);
            for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += ClientProxy.random.nextInt(2);
                }
                if (i == regen) {
                    y -= 2;
                }
                event.getGuiGraphics().blit(MEDIUMCORE_HEARTS_TEXTURE, x, y, 50, heartU, heartV + 18, 9, 9, 64, 32);

                if (i * 2 + 1 < health) {
                    event.getGuiGraphics().blit(MEDIUMCORE_HEARTS_TEXTURE, x, y, 50, heartU, heartV, 9, 9, 64, 32);
                } else if (i * 2 + 1 == health) {
                    event.getGuiGraphics().blit(MEDIUMCORE_HEARTS_TEXTURE, x, y, 50, heartU + 9, heartV, 9, 9, 64, 32);
                }
            }
            event.getGuiGraphics().pose().popPose();
        }
    }

    public void setGameRuleLocalValue(boolean mediumcoreMode) {
        this.mediumcoreMode = mediumcoreMode;
    }

    public boolean isMediumcoreModeLocally() {
        return this.mediumcoreMode;
    }

}
