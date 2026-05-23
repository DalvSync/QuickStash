package org.dalvsync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier; // Використовуємо нову офіційну назву від Mojang
import net.minecraft.sounds.SoundEvent;
import org.dalvsync.network.StashRequestPayload;

public class quickstash implements ModInitializer {
    public static final String MOD_ID = "quickstash";

    public static final Identifier INVENTORY_FULL_ID = Identifier.fromNamespaceAndPath(MOD_ID, "inv_full");
    public static final SoundEvent INVENTORY_FULL_EVENT = SoundEvent.createVariableRangeEvent(INVENTORY_FULL_ID);

    public static final Identifier SUCCESS_ID = Identifier.fromNamespaceAndPath(MOD_ID, "success");
    public static final SoundEvent SUCCESS_EVENT = SoundEvent.createVariableRangeEvent(SUCCESS_ID);

    public static final Identifier WAIT_MESSAGE_ID = Identifier.fromNamespaceAndPath(MOD_ID, "wait_message");
    public static final SoundEvent WAIT_MESSAGE_EVENT = SoundEvent.createVariableRangeEvent(WAIT_MESSAGE_ID);

    @Override
    public void onInitialize() {
        QuickStashConfig.load();

        Registry.register(BuiltInRegistries.SOUND_EVENT, INVENTORY_FULL_ID, INVENTORY_FULL_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SUCCESS_ID, SUCCESS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, WAIT_MESSAGE_ID, WAIT_MESSAGE_EVENT);

        PayloadTypeRegistry.serverboundPlay().register(StashRequestPayload.ID, StashRequestPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StashRequestPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                StashLogic.performStash(context.player());
            });
        });
    }
}