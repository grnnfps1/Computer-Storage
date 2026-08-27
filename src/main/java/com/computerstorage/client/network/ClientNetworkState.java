package com.computerstorage.client.network;

import com.computerstorage.common.network.SyncNetworkStatePacket;

import java.util.List;

/** Client cache populated only by server-authoritative network snapshots. */
public final class ClientNetworkState {
    private static volatile List<SyncNetworkStatePacket.EndpointData> endpoints = List.of();

    private ClientNetworkState() {}

    public static void apply(SyncNetworkStatePacket packet) {
        endpoints = List.copyOf(packet.endpoints());
    }

    public static List<SyncNetworkStatePacket.EndpointData> endpoints() {
        return endpoints;
    }

    public static void clear() {
        endpoints = List.of();
    }
}
