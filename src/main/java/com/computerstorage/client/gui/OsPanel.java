package com.computerstorage.client.gui;

/** Pure presentation model for an OS panel. */
public record OsPanel(OsTab tab, String title, String status) {
    public OsPanel {
        if (tab == null) throw new IllegalArgumentException("tab cannot be null");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title cannot be blank");
        if (status == null) throw new IllegalArgumentException("status cannot be null");
    }
}
