package com.computerstorage.common.network;

import com.computerstorage.common.transfer.TransferEndpoint;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SyncNetworkStatePacketTest {
    @Test
    void endpointStateRoundTrips() {
        var original = new SyncNetworkStatePacket(List.of(
                new TransferEndpoint("input", new BlockPos(1, 2, 3), Direction.NORTH),
                new TransferEndpoint("wireless", new BlockPos(4, 5, 6), null)), true);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        original.encode(buf);
        var decoded = SyncNetworkStatePacket.decode(buf);
        assertEquals(original, decoded);
        assertEquals(1, decoded.endpoints().get(0).pos().getX());
        assertNull(decoded.endpoints().get(1).side());
    }

    @Test
    void endpointCountIsBounded() {
        assertThrows(IllegalArgumentException.class,
                () -> new SyncNetworkStatePacket(java.util.stream.IntStream.range(0, 65)
                        .mapToObj(i -> new SyncNetworkStatePacket.EndpointData("e" + i, BlockPos.ZERO, null)).toList()));
    }
}
