package com.computerstorage.common.transfer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Controller-owned registry of player-configured logistics endpoints. */
public final class TransferEndpointRegistry {
    private final Map<String, TransferEndpoint> endpoints = new LinkedHashMap<>();

    public boolean register(TransferEndpoint endpoint) {
        if (endpoints.containsKey(endpoint.id())) return false;
        endpoints.put(endpoint.id(), endpoint);
        return true;
    }

    public boolean replace(TransferEndpoint endpoint) {
        endpoints.put(endpoint.id(), endpoint);
        return true;
    }

    public boolean remove(String id) { return endpoints.remove(id) != null; }

    public TransferEndpoint get(String id) { return endpoints.get(id); }

    public Collection<TransferEndpoint> all() { return java.util.List.copyOf(endpoints.values()); }

    public void clear() { endpoints.clear(); }
}
