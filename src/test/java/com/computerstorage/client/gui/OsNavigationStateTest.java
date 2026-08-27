package com.computerstorage.client.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsNavigationStateTest {
    @Test
    void startsOnSystemAndChangesSelection() {
        OsNavigationState state = new OsNavigationState();
        assertEquals(OsTab.SYSTEM, state.selected());
        state.select(OsTab.LOGISTICS);
        assertEquals(OsTab.LOGISTICS, state.selected());
        state.select(null);
        assertEquals(OsTab.LOGISTICS, state.selected());
    }
}
