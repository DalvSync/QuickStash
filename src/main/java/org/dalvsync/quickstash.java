package org.dalvsync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.dalvsync.network.StashRequestPayload;

public class quickstash implements ModInitializer {
    public static final String MOD_ID = "quickstash";

    public static final Identifier INVENTORY_FULL_ID = Identifier.of(MOD_ID, "inv_full");
    public static final SoundEvent INVENTORY_FULL_EVENT = SoundEvent.of(INVENTORY_FULL_ID);

    public static final Identifier SUCCESS_ID = Identifier.of(MOD_ID, "success");
    public static final SoundEvent SUCCESS_EVENT = SoundEvent.of(SUCCESS_ID);

    public static final Identifier WAIT_MESSAGE_ID = Identifier.of(MOD_ID, "wait_message");
    public static final SoundEvent WAIT_MESSAGE_EVENT = SoundEvent.of(WAIT_MESSAGE_ID);

    @Override
    public void onInitialize() {
        QuickStashConfig.load();

        Registry.register(Registries.SOUND_EVENT, INVENTORY_FULL_ID, INVENTORY_FULL_EVENT);
        Registry.register(Registries.SOUND_EVENT, SUCCESS_ID, SUCCESS_EVENT);
        Registry.register(Registries.SOUND_EVENT, WAIT_MESSAGE_ID, WAIT_MESSAGE_EVENT);

        PayloadTypeRegistry.playC2S().register(StashRequestPayload.ID, StashRequestPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StashRequestPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                StashLogic.performStash(context.player());
            });
        });
    }
}