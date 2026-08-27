package com.computerstorage.common.transfer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Small bounded runtime log for the OS logistics screen. */
public final class TransferAuditLog {
    private static final int MAX_ENTRIES = 100;
    private final Deque<Entry> entries = new ArrayDeque<>();

    public void record(long gameTime, String programId, int moved) {
        entries.addLast(new Entry(gameTime, programId, moved));
        while (entries.size() > MAX_ENTRIES) entries.removeFirst();
    }

    public List<Entry> entries() { return List.copyOf(entries); }

    public record Entry(long gameTime, String programId, int moved) {}
}
