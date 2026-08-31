package org.dalvsync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StashPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StashPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(quickstash.MOD_ID, "stash_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StashPayload> CODEC = StreamCodec.unit(new StashPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
