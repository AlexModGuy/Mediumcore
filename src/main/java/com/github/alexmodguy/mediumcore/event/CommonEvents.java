package com.github.alexmodguy.mediumcore.event;

import com.github.alexmodguy.mediumcore.GameRuleRegistry;
import com.github.alexmodguy.mediumcore.Mediumcore;
import com.github.alexmodguy.mediumcore.MediumcoreTags;
import com.github.alexmodguy.mediumcore.misc.DedicatedServerPropertiesAccessor;
import com.github.alexmodguy.mediumcore.packet.SyncMediumcoreGameRuleMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class CommonEvents {

    private static final String HEALTH_MODIFIER_TAG = "MediumcoreHealthModifier";
    private static final UUID INITIAL_HEALTH_MODIFIER_UUID = Mth.createInsecureUUID(RandomSource.create(2929292911123L));
    private static final UUID HEALTH_MODIFIER_UUID = Mth.createInsecureUUID(RandomSource.create(111222333441249L));

    @SubscribeEvent
    public void onEntityJoinLevel(ServerStartedEvent event) {
        if(event.getServer().isDedicatedServer() && event.getServer() instanceof DedicatedServer dedicatedServer){
            boolean propertiesSayMediumcore = ((DedicatedServerPropertiesAccessor)dedicatedServer.getProperties()).isServerMediumcore();
            if(propertiesSayMediumcore){
                Mediumcore.LOGGER.info("set server game rule for mediumcore because it is set to true in server.properties");
                GameRuleRegistry.setMediumcoreMode(event.getServer().getGameRules(), propertiesSayMediumcore, event.getServer());
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player && !event.getLevel().isClientSide) {
            boolean mediumcore = GameRuleRegistry.isMediumCoreMode(event.getLevel().getGameRules());
            if (mediumcore && player instanceof ServerPlayer) {
                Mediumcore.sendNonLocal(new SyncMediumcoreGameRuleMessage(mediumcore), (ServerPlayer) player);
                CompoundTag tag = event.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                double healthModifiedBy = tag.getDouble(HEALTH_MODIFIER_TAG);
                updateHealth(player, healthModifiedBy);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (GameRuleRegistry.isMediumCoreMode(player.level().getGameRules())) {
                CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                double clampedHealth = Mth.clamp(player.getMaxHealth() - Mediumcore.CONFIG.healthDecreasePerDeath.get(), Mediumcore.CONFIG.minimumPlayerHealth.get(), Mediumcore.CONFIG.maxPlayerHealth.get());
                double healthModifiedBy = tag.getDouble(HEALTH_MODIFIER_TAG) + (clampedHealth - player.getMaxHealth());
                tag.putDouble(HEALTH_MODIFIER_TAG, healthModifiedBy);
                player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && !event.getEntity().level().isClientSide) {
            if (GameRuleRegistry.isMediumCoreMode(event.getEntity().level().getGameRules())) {
                CompoundTag tag = event.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                double healthModifiedBy = tag.getDouble(HEALTH_MODIFIER_TAG);
                updateHealth(event.getEntity(), healthModifiedBy);
            }
        }
    }

    @SubscribeEvent
    public void onLivingFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getItem().is(MediumcoreTags.RESTORES_MAX_HEALTH) && GameRuleRegistry.isMediumCoreMode(event.getEntity().level().getGameRules())) {
                CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                double clampedHealth = Mth.clamp(player.getMaxHealth() + Mediumcore.CONFIG.healthIncreasePerHeal.get(), Mediumcore.CONFIG.minimumPlayerHealth.get(), Mediumcore.CONFIG.maxPlayerHealth.get());
                double healthModifiedBy = tag.getDouble(HEALTH_MODIFIER_TAG) + (clampedHealth - player.getMaxHealth());
                tag.putDouble(HEALTH_MODIFIER_TAG, healthModifiedBy);
                player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
                updateHealth(player, healthModifiedBy);
            }
        }
    }

    private void updateHealth(Player player, double healthModifiedBy) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        attribute.removePermanentModifier(INITIAL_HEALTH_MODIFIER_UUID);
        attribute.addPermanentModifier(new AttributeModifier(INITIAL_HEALTH_MODIFIER_UUID, "MediumcoreInitialHealthMod", Mediumcore.CONFIG.startingPlayerHealth.get() - 20F, AttributeModifier.Operation.ADDITION));
        attribute.removePermanentModifier(HEALTH_MODIFIER_UUID);
        attribute.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "MediumcoreHealthMod", healthModifiedBy, AttributeModifier.Operation.ADDITION));
        player.setHealth(Mth.clamp(player.getHealth(), 0, player.getMaxHealth()));
    }
}
