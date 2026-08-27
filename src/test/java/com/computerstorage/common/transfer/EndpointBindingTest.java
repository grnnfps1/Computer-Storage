package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndpointBindingTest {
    @Test
    void normalizesPositionAndKeepsSide() {
        EndpointBinding binding = new EndpointBinding("input_chest", new BlockPos(10, 64, -2), Direction.NORTH);
        assertEquals(new BlockPos(10, 64, -2), binding.pos());
        assertEquals(Direction.NORTH, binding.side());
    }

    @Test
    void rejectsInvalidIds() {
        assertThrows(IllegalArgumentException.class,
                () -> new EndpointBinding("bad id", BlockPos.ZERO, null));
    }
}
