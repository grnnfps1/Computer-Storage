package com.computerstorage.common.transfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Programmable logistics network. Routes are explicit and priority ordered;
 * this keeps behavior predictable and leaves room for path discovery later.
 */
public final class TransferNetwork {
    private final Map<String, TransferNode> nodes = new HashMap<>();
    private final List<Route> routes = new ArrayList<>();

    public void registerNode(TransferNode node) { nodes.put(node.id(), node); }
    public void unregisterNode(String id) { nodes.remove(id); }
    public TransferNode node(String id) { return nodes.get(id); }

    public void addRoute(String sourceId, String destinationId, TransferRule rule) {
        if (!nodes.containsKey(sourceId) || !nodes.containsKey(destinationId)) {
            throw new IllegalArgumentException("Both transfer nodes must be registered");
        }
        routes.add(new Route(sourceId, destinationId, rule));
        routes.sort(Comparator.comparingInt((Route r) -> r.rule().priority()).reversed());
    }

    public List<Route> routes() { return List.copyOf(routes); }

    public int tick() {
        int moved = 0;
        for (Route route : routes) {
            TransferNode source = nodes.get(route.sourceId());
            TransferNode destination = nodes.get(route.destinationId());
            if (source == null || destination == null) continue;
            moved += ItemTransferEngine.transfer(source.inventory(), destination.inventory(), route.rule()).moved();
        }
        return moved;
    }

    public record Route(String sourceId, String destinationId, TransferRule rule) {}
}
