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
        assertEquals("input", snapshot.routes().get(0).sourceId());
        assertEquals("storage", snapshot.routes().get(0).destinationId());
        assertEquals(10, snapshot.routes().get(0).priority());
        assertEquals(TransferDirection.IMPORT, snapshot.routes().get(0).direction());
        assertEquals(64, snapshot.routes().get(0).maxItemsPerOperation());
        assertEquals(8, snapshot.routes().get(0).minSourceAmount());
        assertEquals(512, snapshot.routes().get(0).maxDestinationAmount());
    }
}
