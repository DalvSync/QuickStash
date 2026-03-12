package org.dalvsync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class quickstash implements ModInitializer {
    public static final String MOD_ID = "quickstash";

    public static final Identifier STASH_REQUEST_ID = new Identifier(MOD_ID + ":stash_request");

    public static final Identifier INVENTORY_FULL_ID = new Identifier(MOD_ID, "inv_full");
    public static final SoundEvent INVENTORY_FULL_EVENT = SoundEvent.of(INVENTORY_FULL_ID);
    public static final Identifier SUCCESS_ID = new Identifier(MOD_ID, "success");
    public static final SoundEvent SUCCESS_EVENT = SoundEvent.of(SUCCESS_ID);
    public static final Identifier WAIT_MESSAGE_ID = new Identifier(MOD_ID, "wait_message");
    public static final SoundEvent WAIT_MESSAGE_EVENT = SoundEvent.of(WAIT_MESSAGE_ID);

    @Override
    public void onInitialize() {
        QuickStashConfig.load();

        Registry.register(Registries.SOUND_EVENT, INVENTORY_FULL_ID, INVENTORY_FULL_EVENT);

        ServerPlayNetworking.registerGlobalReceiver(STASH_REQUEST_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                StashLogic.performStash(player);
            });
        });
    }
}