package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferProgramRepositoryTest {
    @Test
    void programsAreOrderedByPriorityThenId() {
        TransferProgramStore store = new TransferProgramStore();
        store.add(new TransferProgram("low", "a", "b", new TransferFilter(TransferFilter.Mode.ALL, ""), 10, 1, 0, 0, TransferCondition.ALWAYS, TransferSchedule.everyTick()));
        store.add(new TransferProgram("high", "a", "b", new TransferFilter(TransferFilter.Mode.ALL, ""), 100, 1, 0, 0, TransferCondition.ALWAYS, TransferSchedule.everyTick()));
        store.add(new TransferProgram("middle", "a", "c", new TransferFilter(TransferFilter.Mode.ALL, ""), 50, 1, 0, 0, TransferCondition.ALWAYS, TransferSchedule.everyTick()));

        var repository = new TransferProgramRepository(store);
        assertEquals("high", repository.orderedPrograms().get(0).id());
        assertEquals("middle", repository.orderedPrograms().get(1).id());
        assertEquals(3, repository.matchingSource("a").size());
        assertEquals(1, repository.matching("a", "b").size());
    }
}
