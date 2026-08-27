package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferAuditLogTest {
    @Test
    void logIsBoundedAndKeepsNewestEntries() {
        TransferAuditLog log = new TransferAuditLog();
        for (int i = 0; i < 105; i++) log.record(i, "p", i);
        assertEquals(100, log.entries().size());
        assertEquals(5, log.entries().getFirst().gameTime());
        assertEquals(104, log.entries().getLast().gameTime());
    }
}
