package com.computerstorage.client.gui;

import com.computerstorage.common.network.SyncNetworkStatePacket;

import java.util.List;

/** Client-side snapshot received from the server. */
public final class ClientNetworkState {
    private static List<SyncNetworkStatePacket.EndpointData> endpoints = List.of();

    private ClientNetworkState() {}

    public static void accept(SyncNetworkStatePacket packet) {
        endpoints = List.copyOf(packet.endpoints());
    }

    public static List<SyncNetworkStatePacket.EndpointData> endpoints() {
        return endpoints;
    }
}
