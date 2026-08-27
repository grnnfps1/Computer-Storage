package com.computerstorage.common.transfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** In-memory index used by the controller runtime to resolve enabled programs by priority. */
public final class TransferProgramRepository {
    private final TransferProgramStore store;

    public TransferProgramRepository(TransferProgramStore store) {
        this.store = store;
    }

    public List<TransferProgram> orderedPrograms() {
        return store.programs().stream()
                .sorted(Comparator.comparingInt(TransferProgram::priority).reversed()
                        .thenComparing(TransferProgram::id))
                .toList();
    }

    public List<TransferProgram> matching(String sourceId, String destinationId) {
        return orderedPrograms().stream()
                .filter(p -> p.sourceId().equals(sourceId) && p.destinationId().equals(destinationId))
                .toList();
    }

    public List<TransferProgram> matchingSource(String sourceId) {
        return orderedPrograms().stream()
                .filter(p -> p.sourceId().equals(sourceId))
                .toList();
    }
}
