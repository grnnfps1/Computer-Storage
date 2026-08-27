package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferRuleRuntimeTest {
    @Test
    void alwaysRuleRunsOnlyOnceAtScheduledTick() {
        TransferRuleRuntime runtime = new TransferRuleRuntime(new TransferSchedule(20, 10), TransferCondition.ALWAYS);
        assertTrue(runtime.due(10, false));
        assertTrue(runtime.markRun(10));
        assertFalse(runtime.due(10, false));
        assertFalse(runtime.due(11, false));
        assertTrue(runtime.due(30, false));
    }

    @Test
    void redstoneConditionsGateExecution() {
        TransferRuleRuntime high = new TransferRuleRuntime(TransferSchedule.everyTick(), TransferCondition.REDSTONE_HIGH);
        TransferRuleRuntime low = new TransferRuleRuntime(TransferSchedule.everyTick(), TransferCondition.REDSTONE_LOW);
        assertFalse(high.due(1, false));
        assertTrue(high.due(2, true));
        assertTrue(low.due(1, false));
        assertFalse(low.due(2, true));
    }
}
