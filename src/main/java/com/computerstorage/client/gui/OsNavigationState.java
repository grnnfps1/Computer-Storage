package com.computerstorage.client.gui;

/** Client-only navigation state; it deliberately contains no server/game state. */
public final class OsNavigationState {
    private OsTab selected = OsTab.SYSTEM;

    public OsTab selected() { return selected; }

    public void select(OsTab tab) {
        if (tab != null) selected = tab;
    }
}
