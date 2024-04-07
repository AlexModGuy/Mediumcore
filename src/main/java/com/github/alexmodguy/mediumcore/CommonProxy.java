package com.github.alexmodguy.mediumcore;

import com.github.alexmodguy.mediumcore.event.CommonEvents;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
    public void commonInit() {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    public void clientInit() {
    }

    public void setGameRuleLocalValue(boolean mediumcoreMode) {
    }

    public boolean isMediumcoreModeLocally() {
        return false;
    }
}
