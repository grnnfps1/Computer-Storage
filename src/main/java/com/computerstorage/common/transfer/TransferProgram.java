package com.computerstorage.common.transfer;

import java.util.Objects;

public record TransferProgram(
        String id, String sourceId, String destinationId, TransferFilter filter,
        int priority, int maxItemsPerOperation, int minSourceAmount, int maxDestinationAmount,
        TransferCondition condition, TransferSchedule schedule, boolean enabled) {
    public TransferProgram {
        Objects.requireNonNull(id); Objects.requireNonNull(sourceId); Objects.requireNonNull(destinationId);
        Objects.requireNonNull(filter); Objects.requireNonNull(condition); Objects.requireNonNull(schedule);
        if (id.isBlank() || sourceId.isBlank() || destinationId.isBlank()) throw new IllegalArgumentException("Program identifiers cannot be blank");
        if (priority < 0) throw new IllegalArgumentException("priority cannot be negative");
        if (maxItemsPerOperation < 1) throw new IllegalArgumentException("maxItemsPerOperation must be positive");
        if (minSourceAmount < 0 || maxDestinationAmount < 0) throw new IllegalArgumentException("amount limits cannot be negative");
    }

    public TransferProgram(String id, String sourceId, String destinationId, TransferFilter filter, int priority,
                           int maxItemsPerOperation, int minSourceAmount, int maxDestinationAmount,
                           TransferCondition condition, TransferSchedule schedule) {
        this(id, sourceId, destinationId, filter, priority, maxItemsPerOperation, minSourceAmount, maxDestinationAmount, condition, schedule, true);
    }
}
