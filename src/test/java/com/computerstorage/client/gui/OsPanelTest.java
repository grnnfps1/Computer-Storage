package com.computerstorage.client.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OsPanelTest {
    @Test
    void presentationModelKeepsPanelIdentity() {
        OsPanel panel = new OsPanel(OsTab.NETWORK, "NETWORK", "ONLINE");
        assertEquals(OsTab.NETWORK, panel.tab());
        assertEquals("NETWORK", panel.title());
        assertEquals("ONLINE", panel.status());
    }

    @Test
    void invalidPanelIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new OsPanel(OsTab.SYSTEM, "", "ONLINE"));
    }
}
