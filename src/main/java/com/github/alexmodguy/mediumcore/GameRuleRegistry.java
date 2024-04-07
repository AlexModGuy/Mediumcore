package com.github.alexmodguy.mediumcore;

import com.github.alexmodguy.mediumcore.packet.SyncMediumcoreGameRuleMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import javax.annotation.Nullable;

public class GameRuleRegistry {
    private static GameRules.Key<GameRules.BooleanValue> mediumcoreMode;

    public static void setup() {
        mediumcoreMode = createBoolean("mediumcoreMode", false, GameRules.Category.PLAYER);
    }

    public static GameRules.Key<GameRules.BooleanValue> createBoolean(String id, boolean defaultVal, GameRules.Category cat) {
        try {
            GameRules.Type<GameRules.BooleanValue> ruleTypeBoolean = GameRules.BooleanValue.create(defaultVal, ((server, booleanValue) -> {
                Mediumcore.sendMSGToAll(server, new SyncMediumcoreGameRuleMessage(booleanValue.get()));
            }));
            GameRules.Key<GameRules.BooleanValue> rule = GameRules.register(id, cat, ruleTypeBoolean);
            Mediumcore.LOGGER.info("Created game rule {}", id);
            return rule;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isMediumCoreMode(GameRules gameRules){
        return mediumcoreMode != null && gameRules.getBoolean(mediumcoreMode);
    }

    public static void setMediumcoreMode(GameRules gameRules, boolean mediumcore, @Nullable MinecraftServer minecraftServer){
        if(mediumcoreMode != null){
            gameRules.getRule(mediumcoreMode).set(mediumcore, minecraftServer);
        }
    }
}
