package com.computerstorage.client.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsPanelRendererTest {
    @Test
    void navigationOrderMatchesOsContract() {
        assertEquals(0, OsTab.SYSTEM.ordinal());
        assertEquals(1, OsTab.HARDWARE.ordinal());
        assertEquals(2, OsTab.STORAGE.ordinal());
        assertEquals(3, OsTab.NETWORK.ordinal());
        assertEquals(4, OsTab.LOGISTICS.ordinal());
    }
}
