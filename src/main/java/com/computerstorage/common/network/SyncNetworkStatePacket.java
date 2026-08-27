package com.computerstorage.common.network;

import com.computerstorage.common.transfer.TransferEndpoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public record SyncNetworkStatePacket(List<EndpointData> endpoints) implements NetworkMessage {
    public static final int MAX_ENDPOINTS = 64;

    public SyncNetworkStatePacket {
        if (endpoints == null || endpoints.size() > MAX_ENDPOINTS) throw new IllegalArgumentException("Invalid endpoint count");
        endpoints = List.copyOf(endpoints);
    }

    public SyncNetworkStatePacket(List<TransferEndpoint> endpoints, boolean marker) {
        this(endpoints.stream().map(EndpointData::from).toList());
    }

    @Override public int protocolVersion() { return NetworkConstants.PROTOCOL_VERSION; }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(endpoints.size());
        for (EndpointData endpoint : endpoints) endpoint.encode(buf);
    }

    public static SyncNetworkStatePacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        if (count < 0 || count > MAX_ENDPOINTS) throw new IllegalArgumentException("Invalid endpoint count");
        var result = new java.util.ArrayList<EndpointData>(count);
        for (int i = 0; i < count; i++) result.add(EndpointData.decode(buf));
        return new SyncNetworkStatePacket(result);
    }

    public record EndpointData(String id, BlockPos pos, Direction side) {
        static EndpointData from(TransferEndpoint endpoint) { return new EndpointData(endpoint.id(), endpoint.pos(), endpoint.side()); }
        void encode(FriendlyByteBuf buf) {
            NetworkPackets.writeString(buf, id, 64);
            buf.writeBlockPos(pos);
            buf.writeBoolean(side != null);
            if (side != null) buf.writeEnum(side);
        }
        static EndpointData decode(FriendlyByteBuf buf) {
            String id = NetworkPackets.readString(buf, 64);
            BlockPos pos = buf.readBlockPos();
            Direction side = buf.readBoolean() ? buf.readEnum(Direction.class) : null;
            return new EndpointData(id, pos, side);
        }
    }
}
