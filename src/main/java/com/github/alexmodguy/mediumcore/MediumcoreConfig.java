package com.github.alexmodguy.mediumcore;

import net.minecraftforge.common.ForgeConfigSpec;

public class MediumcoreConfig {

    public final ForgeConfigSpec.BooleanValue mediumcoreDefaultGameMode;
    public final ForgeConfigSpec.BooleanValue mediumcoreHeartTexture;
    public final ForgeConfigSpec.DoubleValue startingPlayerHealth;
    public final ForgeConfigSpec.DoubleValue minimumPlayerHealth;
    public final ForgeConfigSpec.DoubleValue maxPlayerHealth;
    public final ForgeConfigSpec.DoubleValue healthDecreasePerDeath;
    public final ForgeConfigSpec.DoubleValue healthIncreasePerHeal;

    public MediumcoreConfig(final ForgeConfigSpec.Builder builder) {
        mediumcoreDefaultGameMode = builder.comment("Whether mediumcore appears as the first, default game mode when creating a new world.").translation("mediumcore_default_game_mode").define("mediumcore_default_game_mode", false);
        mediumcoreHeartTexture = builder.comment("Whether hearts appear with a different texture in mediumcore.").translation("mediumcore_heart_texture").define("mediumcore_heart_texture", true);
        startingPlayerHealth = builder.comment("The amount of max health each player starts with in mediumcore. Default is 20 HP, which is 10 hearts.").translation("starting_player_health").defineInRange("starting_player_health", 20.0D, 0.5D, 1000.0D);
        minimumPlayerHealth = builder.comment("The lowest amount of max health a player can have in mediumcore. Health will not decrease beyond this threshold no matter how many deaths are taken. Default is 6 HP, which is 3 hearts.").translation("minimum_player_health").defineInRange("minimum_player_health", 6.0D, 0.5D, 1000.0D);
        maxPlayerHealth = builder.comment("The amount of max health each player can possibly have in mediumcore. Default is 20 HP, which is 10 hearts.").translation("max_player_health").defineInRange("max_player_health", 20.0D, 0.5D, 1000.0D);
        healthDecreasePerDeath = builder.comment("The amount of max health lost with each death. Default is 2 HP, which is 1 heart.").translation("health_decrease_per_death").defineInRange("health_decrease_per_death", 2.0D, 0.0D, 20.0D);
        healthIncreasePerHeal = builder.comment("The amount of max health regained when healed. Default is 2 HP, which is 1 heart.").translation("health_decrease_per_death").defineInRange("health_increase_per_heal", 2.0D, 0.0D, 20.0D);
    }
}