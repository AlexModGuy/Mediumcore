package com.github.alexmodguy.mediumcore.mixins;

import com.github.alexmodguy.mediumcore.Mediumcore;
import com.github.alexmodguy.mediumcore.misc.DedicatedServerPropertiesAccessor;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Properties;

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin extends Settings implements DedicatedServerPropertiesAccessor {

    private final boolean mediumcore = this.get("mediumcore", Mediumcore.CONFIG.mediumcoreDefaultGameMode.get());

    public DedicatedServerPropertiesMixin(Properties properties) {
        super(properties);
    }

    public boolean isServerMediumcore() {
        return mediumcore;
    }
}
