package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferEndpointRegistryTest {
    @Test
    void registryPreventsDuplicateRegistrationAndAllowsReplacement() {
        TransferEndpointRegistry registry = new TransferEndpointRegistry();
        var first = new TransferEndpoint("input", BlockPos.ZERO, Direction.NORTH);
        var replacement = new TransferEndpoint("input", new BlockPos(1, 2, 3), Direction.SOUTH);

        assertTrue(registry.register(first));
        assertFalse(registry.register(first));
        assertTrue(registry.replace(replacement));
        assertEquals(new BlockPos(1, 2, 3), registry.get("input").pos());
        assertEquals(1, registry.all().size());
        assertTrue(registry.remove("input"));
        assertNull(registry.get("input"));
    }
}
