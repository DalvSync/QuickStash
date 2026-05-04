package org.dalvsync.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StashRequestPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StashRequestPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("quickstash", "stash_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StashRequestPayload> CODEC = StreamCodec.unit(new StashRequestPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}