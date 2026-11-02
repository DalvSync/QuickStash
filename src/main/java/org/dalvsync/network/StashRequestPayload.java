package org.dalvsync.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StashRequestPayload(long lockedSlotsMask) implements CustomPayload {
    public static final CustomPayload.Id<StashRequestPayload> ID = new CustomPayload.Id<>(Identifier.of("quickstash", "stash_request"));
    public static final PacketCodec<RegistryByteBuf, StashRequestPayload> CODEC = PacketCodec.of(
            (payload, buffer) -> buffer.writeVarLong(payload.lockedSlotsMask()),
            buffer -> new StashRequestPayload(buffer.readVarLong())
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
