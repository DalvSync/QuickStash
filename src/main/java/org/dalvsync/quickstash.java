package org.dalvsync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

public class quickstash implements ModInitializer {
    public static final String MOD_ID = "quickstash";

    public static final ResourceLocation INVENTORY_FULL_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "inv_full");
    public static final SoundEvent INVENTORY_FULL_EVENT = SoundEvent.createVariableRangeEvent(INVENTORY_FULL_ID);
    public static final ResourceLocation SUCCESS_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "success");
    public static final SoundEvent SUCCESS_EVENT = SoundEvent.createVariableRangeEvent(SUCCESS_ID);
    public static final ResourceLocation WAIT_MESSAGE_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "wait_message");
    public static final SoundEvent WAIT_MESSAGE_EVENT = SoundEvent.createVariableRangeEvent(WAIT_MESSAGE_ID);

    @Override
    public void onInitialize() {
        QuickStashConfig.load();

        Registry.register(BuiltInRegistries.SOUND_EVENT, INVENTORY_FULL_ID, INVENTORY_FULL_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SUCCESS_ID, SUCCESS_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, WAIT_MESSAGE_ID, WAIT_MESSAGE_EVENT);

        PayloadTypeRegistry.playC2S().register(StashPayload.TYPE, StashPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StashPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                StashLogic.performStash(context.player());
            });
        });
    }
}