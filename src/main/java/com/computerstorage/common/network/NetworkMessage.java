package com.computerstorage.common.network;

/** Marker contract for messages exchanged by the Computer Storage client/server channel. */
public interface NetworkMessage {
    int protocolVersion();
}
