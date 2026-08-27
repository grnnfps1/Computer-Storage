package com.computerstorage.client.gui;

import com.computerstorage.common.transfer.TransferEndpoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkPanelModelTest {
    @Test
    void exposesImmutableEndpointSnapshot() {
        NetworkPanelModel model = new NetworkPanelModel(List.of(
                new TransferEndpoint("input", BlockPos.ZERO, Direction.NORTH),
                new TransferEndpoint("output", new BlockPos(1, 2, 3), Direction.SOUTH)));

        assertEquals(2, model.endpoints().size());
        assertEquals(2, model.onlineCount());
        assertThrows(UnsupportedOperationException.class,
                () -> model.endpoints().add(new TransferEndpoint("x", BlockPos.ZERO, null)));
    }
}
