package org.dalvsync;

import org.dalvsync.network.StashRequestPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class quickstash implements ModInitializer {
    public static final String MOD_ID = "quickstash";

    @Override
    public void onInitialize() {
        QuickStashConfig.load();

        PayloadTypeRegistry.playC2S().register(StashRequestPayload.ID, StashRequestPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(StashRequestPayload.ID, (payload, context) -> {
            context.player().getServer().execute(() -> {
                StashLogic.performStash(context.player());
            });
        });
    }
}