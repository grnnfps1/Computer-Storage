package com.computerstorage.common.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkConstantsTest {
    @Test
    void protocolVersionIsStable() {
        assertEquals(1, NetworkConstants.PROTOCOL_VERSION);
    }
}
