package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferScheduleTest {
    @Test
    void everyTenTicksRunsOnlyOnSchedule() {
        TransferSchedule schedule = new TransferSchedule(10, 5);
        assertFalse(schedule.shouldRun(4));
        assertTrue(schedule.shouldRun(5));
        assertFalse(schedule.shouldRun(14));
        assertTrue(schedule.shouldRun(15));
    }

    @Test
    void invalidScheduleIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TransferSchedule(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new TransferSchedule(1, -1));
    }
}
