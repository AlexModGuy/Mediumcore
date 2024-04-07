package com.github.alexmodguy.mediumcore;

import com.github.alexmodguy.mediumcore.client.ClientProxy;
import com.github.alexmodguy.mediumcore.packet.SyncMediumcoreGameRuleMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mediumcore")
public class Mediumcore {
    public static final Logger LOGGER = LogManager.getLogger("mediumcore");

    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation("mediumcore:main_channel");
    public static final SimpleChannel NETWORK_WRAPPER = NetworkRegistry.ChannelBuilder
            .named(PACKET_NETWORK_NAME)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final MediumcoreConfig CONFIG;

    static {
        {
            final Pair<MediumcoreConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(MediumcoreConfig::new);
            CONFIG = specPair.getLeft();
            CONFIG_SPEC = specPair.getRight();
        }
    }

    public Mediumcore() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        GameRuleRegistry.setup();
        PROXY.commonInit();
        int packetsRegistered = 0;
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, SyncMediumcoreGameRuleMessage.class, SyncMediumcoreGameRuleMessage::write, SyncMediumcoreGameRuleMessage::read, SyncMediumcoreGameRuleMessage::handle);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        sendMSGToAll(ServerLifecycleHooks.getCurrentServer(), message);
    }

    public static <MSG> void sendMSGToAll(MinecraftServer server, MSG message) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientInit());
    }

}
