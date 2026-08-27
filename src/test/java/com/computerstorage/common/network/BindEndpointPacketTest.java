package com.computerstorage.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BindEndpointPacketTest {
    @Test
    void roundTripsBindingData() {
        var original = new BindEndpointPacket("input_1", new BlockPos(12, 64, -4), Direction.WEST);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        original.encode(buf);
        assertEquals(original, BindEndpointPacket.decode(buf));
    }
}
