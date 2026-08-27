package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferEndpointTest {
    @Test
    void endpointCapturesStableWorldIdentity() {
        TransferEndpoint endpoint = new TransferEndpoint("input", new BlockPos(10, 64, -3), Direction.NORTH);
        assertEquals("input", endpoint.id());
        assertEquals(new BlockPos(10, 64, -3), endpoint.pos());
        assertEquals(Direction.NORTH, endpoint.side());
    }

    @Test
    void blankIdIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransferEndpoint("", BlockPos.ZERO, null));
    }
}
