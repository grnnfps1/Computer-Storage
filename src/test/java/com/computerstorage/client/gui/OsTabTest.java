package com.computerstorage.client.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsTabTest {
    @Test
    void exposesStableOsNavigationLabels() {
        assertEquals("SYSTEM", OsTab.SYSTEM.label());
        assertEquals("HARDWARE", OsTab.HARDWARE.label());
        assertEquals("STORAGE", OsTab.STORAGE.label());
        assertEquals("NETWORK", OsTab.NETWORK.label());
        assertEquals("LOGISTICS", OsTab.LOGISTICS.label());
    }
}
