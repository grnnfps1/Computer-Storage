package com.computerstorage.client.gui;

public enum OsTab {
    SYSTEM("SYSTEM"),
    HARDWARE("HARDWARE"),
    STORAGE("STORAGE"),
    NETWORK("NETWORK"),
    LOGISTICS("LOGISTICS");

    private final String label;
    OsTab(String label) { this.label = label; }
    public String label() { return label; }
}
