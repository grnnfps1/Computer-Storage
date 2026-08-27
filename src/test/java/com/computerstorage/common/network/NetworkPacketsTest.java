package com.computerstorage.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkPacketsTest {
    @Test
    void boundedStringRoundTrips() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkPackets.writeString(buf, "INPUT_CHEST", 64);
        assertEquals("INPUT_CHEST", NetworkPackets.readString(buf, 64));
    }

    @Test
    void oversizedStringIsRejectedBeforeWrite() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        assertThrows(IllegalArgumentException.class,
                () -> NetworkPackets.writeString(buf, "123456", 5));
    }
}
