package org.dalvsync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class quickstash implements ModInitializer {
    public static final String MOD_ID = "quickstash";

    public static final Identifier STASH_REQUEST_ID = new Identifier(MOD_ID + ":stash_request");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(STASH_REQUEST_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                StashLogic.performStash(player);
            });
        });
    }
}