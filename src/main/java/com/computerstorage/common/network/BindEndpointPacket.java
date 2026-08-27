package com.computerstorage.common.network;

import com.computerstorage.common.transfer.EndpointBindingService;
import com.computerstorage.common.transfer.WorldTransferEndpointRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record BindEndpointPacket(String id, BlockPos pos, Direction side) implements NetworkMessage {
    public static final int MAX_ID_LENGTH = 64;
    @Override public int protocolVersion() { return NetworkConstants.PROTOCOL_VERSION; }
    public void encode(FriendlyByteBuf buf) {
        NetworkPackets.writeString(buf, id, MAX_ID_LENGTH);
        buf.writeBlockPos(pos);
        buf.writeBoolean(side != null);
        if (side != null) buf.writeEnum(side);
    }
    public static BindEndpointPacket decode(FriendlyByteBuf buf) {
        return new BindEndpointPacket(NetworkPackets.readString(buf, MAX_ID_LENGTH), buf.readBlockPos(),
                buf.readBoolean() ? buf.readEnum(Direction.class) : null);
    }
    public EndpointBindingService.Result validateAndBind(ServerPlayer player, WorldTransferEndpointRegistry registry) {
        return EndpointBindingService.bind(player, registry, id, pos, side);
    }
}
