package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferNetworkSnapshotTest {
    @Test
    void snapshotCopiesRouteListAndPreservesConfiguration() {
        TransferNetworkSnapshot snapshot = new TransferNetworkSnapshot(
                2,
                1,
                List.of(new TransferNetworkSnapshot.RouteSnapshot(
                        "input", "storage", 10, TransferDirection.IMPORT, 64, 8, 512)));

        assertEquals(2, snapshot.nodeCount());
        assertEquals(1, snapshot.routeCount());
        assertEquals("input", snapshot.routes().getFirst().sourceId());
        assertEquals("storage", snapshot.routes().getFirst().destinationId());
        assertEquals(10, snapshot.routes().getFirst().priority());
        assertEquals(TransferDirection.IMPORT, snapshot.routes().getFirst().direction());
        assertEquals(64, snapshot.routes().getFirst().maxItemsPerOperation());
        assertEquals(8, snapshot.routes().getFirst().minSourceAmount());
        assertEquals(512, snapshot.routes().getFirst().maxDestinationAmount());
    }
}
