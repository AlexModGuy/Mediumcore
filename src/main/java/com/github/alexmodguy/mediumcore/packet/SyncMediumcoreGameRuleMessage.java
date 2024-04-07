package com.github.alexmodguy.mediumcore.packet;

import com.github.alexmodguy.mediumcore.Mediumcore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncMediumcoreGameRuleMessage {

    private boolean mediumcoreMode;

    public SyncMediumcoreGameRuleMessage(boolean mediumcoreMode) {
        this.mediumcoreMode = mediumcoreMode;
    }


    public static SyncMediumcoreGameRuleMessage read(FriendlyByteBuf buf) {
        return new SyncMediumcoreGameRuleMessage(buf.readBoolean());
    }

    public static void write(SyncMediumcoreGameRuleMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.mediumcoreMode);
    }

    public static void handle(SyncMediumcoreGameRuleMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        Mediumcore.PROXY.setGameRuleLocalValue(message.mediumcoreMode);
    }

}
