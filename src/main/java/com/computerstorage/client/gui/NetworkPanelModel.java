package com.computerstorage.client.gui;

import com.computerstorage.common.transfer.TransferEndpoint;

import java.util.List;

/** Read-only client presentation data for the Network panel. */
public record NetworkPanelModel(List<TransferEndpoint> endpoints) {
    public NetworkPanelModel {
        endpoints = List.copyOf(endpoints);
    }

    public int onlineCount() {
        return endpoints.size();
    }
}
