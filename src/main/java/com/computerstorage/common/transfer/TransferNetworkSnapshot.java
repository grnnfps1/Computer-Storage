package com.computerstorage.common.transfer;

import java.util.List;

/** Immutable diagnostic view of a logistics network. */
public record TransferNetworkSnapshot(int nodeCount, int routeCount, List<RouteSnapshot> routes) {
    public TransferNetworkSnapshot {
        routes = List.copyOf(routes);
    }

    public record RouteSnapshot(String sourceId, String destinationId, int priority,
                                TransferDirection direction, int maxItemsPerOperation,
                                int minSourceAmount, int maxDestinationAmount) {}
}
